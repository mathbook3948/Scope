package dev.mathbook3948.scope.facade;

import dev.mathbook3948.scope.domain.guild.GuildService;
import dev.mathbook3948.scope.domain.guild.member.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuildMemberFacadeTest {

    @InjectMocks
    GuildMemberFacade guildMemberFacade;

    @Mock
    GuildMemberService guildMemberService;

    @Mock
    GuildMemberEventService guildMemberEventService;

    @Mock
    GuildMemberStatService guildMemberStatService;

    @Mock
    GuildService guildService;

    @Test
    @DisplayName("스냅샷이 한 번도 없으면 EPOCH 기준으로 모든 이벤트를 카운트한다")
    void aggregateGuildMemberStats_noStatHistory_countsAllEventsFromEpoch() {
        // given
        Long guildId = 1L;

        when(guildService.findAllGuildIds()).thenReturn(List.of(guildId));
        when(guildMemberStatService.findLatestCreatedAtPerGuild()).thenReturn(Map.of());
        when(guildMemberEventService.countByGuildAndTypeAfter(eq(List.of(guildId)), eq(Instant.EPOCH), any(Instant.class)))
            .thenReturn(List.of(
                new Object[]{guildId, GuildMemberEventType.JOIN, 2L},
                new Object[]{guildId, GuildMemberEventType.LEAVE, 1L}
            ));
        when(guildMemberService.countPerGuild()).thenReturn(Map.of(guildId, 1L));

        // when
        guildMemberFacade.aggregateGuildMemberStats();

        // then
        verify(guildMemberStatService).createGuildMemberStat(eq(guildId), eq(2), eq(1), eq(1), any(Instant.class));
        verifyNoMoreInteractions(guildMemberStatService, guildMemberEventService, guildMemberService);
    }

    @Test
    @DisplayName("이벤트가 없으면 가입 0, 탈퇴 0으로 스냅샷을 생성한다")
    void aggregateGuildMemberStats_noEvents_createsSnapshotWithZeroCounts() {
        // given
        Long guildId = 1L;
        Instant lastStatAt = Instant.parse("2026-04-10T00:00:00Z");

        when(guildService.findAllGuildIds()).thenReturn(List.of(guildId));
        when(guildMemberStatService.findLatestCreatedAtPerGuild()).thenReturn(Map.of(guildId, lastStatAt));
        when(guildMemberEventService.countByGuildAndTypeAfter(eq(List.of(guildId)), eq(lastStatAt), any(Instant.class)))
            .thenReturn(List.of());
        when(guildMemberService.countPerGuild()).thenReturn(Map.of(guildId, 10L));

        // when
        guildMemberFacade.aggregateGuildMemberStats();

        // then
        verify(guildMemberStatService).createGuildMemberStat(eq(guildId), eq(0), eq(0), eq(10), any(Instant.class));
        verifyNoMoreInteractions(guildMemberStatService, guildMemberEventService, guildMemberService);
    }

    @Test
    @DisplayName("길드들이 같은 since를 공유하면 단일 그룹 쿼리로 집계한다")
    void aggregateGuildMemberStats_sharedSince_singleGroupedQuery() {
        // given
        Long guild1 = 1L;
        Long guild2 = 2L;
        Instant lastStatAt = Instant.parse("2026-04-10T00:00:00Z");

        when(guildService.findAllGuildIds()).thenReturn(List.of(guild1, guild2));
        when(guildMemberStatService.findLatestCreatedAtPerGuild())
            .thenReturn(Map.of(guild1, lastStatAt, guild2, lastStatAt));
        when(guildMemberEventService.countByGuildAndTypeAfter(argThat(containsExactlyInAnyOrder(guild1, guild2)), eq(lastStatAt), any(Instant.class)))
            .thenReturn(List.of(
                new Object[]{guild1, GuildMemberEventType.JOIN, 3L},
                new Object[]{guild2, GuildMemberEventType.LEAVE, 1L}
            ));
        when(guildMemberService.countPerGuild()).thenReturn(Map.of(guild1, 20L, guild2, 5L));

        // when
        guildMemberFacade.aggregateGuildMemberStats();

        // then
        verify(guildMemberEventService, times(1)).countByGuildAndTypeAfter(any(), any(), any());
        verify(guildMemberStatService).createGuildMemberStat(eq(guild1), eq(3), eq(0), eq(20), any(Instant.class));
        verify(guildMemberStatService).createGuildMemberStat(eq(guild2), eq(0), eq(1), eq(5), any(Instant.class));
        verifyNoMoreInteractions(guildMemberStatService, guildMemberEventService, guildMemberService);
    }

    @Test
    @DisplayName("길드별 since가 다르면 since별로 그룹화하여 각각 쿼리한다")
    void aggregateGuildMemberStats_distinctSince_queriesPerSinceGroup() {
        // given
        Long guildWithHistory = 1L;
        Long guildWithoutHistory = 2L;
        Instant lastStatAt = Instant.parse("2026-04-10T00:00:00Z");

        when(guildService.findAllGuildIds()).thenReturn(List.of(guildWithHistory, guildWithoutHistory));
        when(guildMemberStatService.findLatestCreatedAtPerGuild())
            .thenReturn(Map.of(guildWithHistory, lastStatAt));

        // history가 있는 길드는 lastStatAt 기준으로, 없는 길드는 EPOCH 기준으로 별도 쿼리
        when(guildMemberEventService.countByGuildAndTypeAfter(eq(List.of(guildWithHistory)), eq(lastStatAt), any(Instant.class)))
            .thenReturn(List.<Object[]>of(new Object[]{guildWithHistory, GuildMemberEventType.JOIN, 1L}));
        when(guildMemberEventService.countByGuildAndTypeAfter(eq(List.of(guildWithoutHistory)), eq(Instant.EPOCH), any(Instant.class)))
            .thenReturn(List.<Object[]>of(
                new Object[]{guildWithoutHistory, GuildMemberEventType.JOIN, 1L},
                new Object[]{guildWithoutHistory, GuildMemberEventType.LEAVE, 1L}
            ));

        when(guildMemberService.countPerGuild()).thenReturn(Map.of(guildWithHistory, 20L, guildWithoutHistory, 5L));

        // when
        guildMemberFacade.aggregateGuildMemberStats();

        // then
        verify(guildMemberStatService).createGuildMemberStat(eq(guildWithHistory), eq(1), eq(0), eq(20), any(Instant.class));
        verify(guildMemberStatService).createGuildMemberStat(eq(guildWithoutHistory), eq(1), eq(1), eq(5), any(Instant.class));
        verifyNoMoreInteractions(guildMemberStatService, guildMemberEventService, guildMemberService);
    }

    @Test
    @DisplayName("같은 cron 실행에서 stat의 createdAt과 쿼리 runAt 상한이 모두 동일한 Instant이다")
    void aggregateGuildMemberStats_singleRun_runAtSharedAcrossQueryAndStat() {
        // given
        Long guild1 = 1L;
        Long guild2 = 2L;
        Instant lastStatAt = Instant.parse("2026-04-10T00:00:00Z");

        when(guildService.findAllGuildIds()).thenReturn(List.of(guild1, guild2));
        when(guildMemberStatService.findLatestCreatedAtPerGuild())
            .thenReturn(Map.of(guild1, lastStatAt, guild2, lastStatAt));
        when(guildMemberEventService.countByGuildAndTypeAfter(argThat(containsExactlyInAnyOrder(guild1, guild2)), eq(lastStatAt), any(Instant.class)))
            .thenReturn(List.of());
        when(guildMemberService.countPerGuild()).thenReturn(Map.of(guild1, 1L, guild2, 2L));

        // when
        guildMemberFacade.aggregateGuildMemberStats();

        // then
        ArgumentCaptor<Instant> statCreatedAtCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(guildMemberStatService, times(2))
            .createGuildMemberStat(anyLong(), anyInt(), anyInt(), anyInt(), statCreatedAtCaptor.capture());
        List<Instant> statCreatedAts = statCreatedAtCaptor.getAllValues();
        assertThat(statCreatedAts).hasSize(2);
        assertThat(statCreatedAts.get(0)).isEqualTo(statCreatedAts.get(1));

        ArgumentCaptor<Instant> queryRunAtCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(guildMemberEventService).countByGuildAndTypeAfter(any(), any(), queryRunAtCaptor.capture());
        assertThat(queryRunAtCaptor.getValue()).isEqualTo(statCreatedAts.get(0));
    }

    @Test
    @DisplayName("길드가 없으면 스냅샷 생성을 중단한다")
    void aggregateGuildMemberStats_noGuilds_skipsSnapshotCreation() {
        // given
        when(guildService.findAllGuildIds()).thenReturn(List.of());

        // when
        guildMemberFacade.aggregateGuildMemberStats();

        // then
        verifyNoInteractions(guildMemberStatService, guildMemberEventService, guildMemberService);
    }

    /**
     * Facade 내부의 그룹 리스트 순서(HashMap 기반)에 의존하지 않도록 원소 집합만 비교한다.
     */
    private static org.mockito.ArgumentMatcher<List<Long>> containsExactlyInAnyOrder(Long... expected) {
        return list -> list != null
            && list.size() == expected.length
            && list.containsAll(List.of(expected));
    }
}
