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
                new GuildMemberEventCountView(guildId, GuildMemberEventType.JOIN, 2L),
                new GuildMemberEventCountView(guildId, GuildMemberEventType.LEAVE, 1L)
            ));
        when(guildMemberService.countPerGuild()).thenReturn(Map.of(guildId, 1L));

        // when
        guildMemberFacade.aggregateGuildMemberStats();

        // then
        List<GuildMemberStat> stats = captureCreatedStats();
        assertThat(stats).hasSize(1);
        GuildMemberStat stat = stats.get(0);
        assertThat(stat.getGuildId()).isEqualTo(guildId);
        assertThat(stat.getJoinedMembers()).isEqualTo(2);
        assertThat(stat.getLeftMembers()).isEqualTo(1);
        assertThat(stat.getTotalMembers()).isEqualTo(1);
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
        List<GuildMemberStat> stats = captureCreatedStats();
        assertThat(stats).hasSize(1);
        GuildMemberStat stat = stats.get(0);
        assertThat(stat.getGuildId()).isEqualTo(guildId);
        assertThat(stat.getJoinedMembers()).isEqualTo(0);
        assertThat(stat.getLeftMembers()).isEqualTo(0);
        assertThat(stat.getTotalMembers()).isEqualTo(10);
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
                new GuildMemberEventCountView(guild1, GuildMemberEventType.JOIN, 3L),
                new GuildMemberEventCountView(guild2, GuildMemberEventType.LEAVE, 1L)
            ));
        when(guildMemberService.countPerGuild()).thenReturn(Map.of(guild1, 20L, guild2, 5L));

        // when
        guildMemberFacade.aggregateGuildMemberStats();

        // then
        verify(guildMemberEventService, times(1)).countByGuildAndTypeAfter(any(), any(), any());
        List<GuildMemberStat> stats = captureCreatedStats();
        assertThat(stats).hasSize(2);
        GuildMemberStat stat1 = stats.stream().filter(s -> s.getGuildId().equals(guild1)).findFirst().orElseThrow();
        assertThat(stat1.getJoinedMembers()).isEqualTo(3);
        assertThat(stat1.getLeftMembers()).isEqualTo(0);
        assertThat(stat1.getTotalMembers()).isEqualTo(20);
        GuildMemberStat stat2 = stats.stream().filter(s -> s.getGuildId().equals(guild2)).findFirst().orElseThrow();
        assertThat(stat2.getJoinedMembers()).isEqualTo(0);
        assertThat(stat2.getLeftMembers()).isEqualTo(1);
        assertThat(stat2.getTotalMembers()).isEqualTo(5);
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
            .thenReturn(List.of(new GuildMemberEventCountView(guildWithHistory, GuildMemberEventType.JOIN, 1L)));
        when(guildMemberEventService.countByGuildAndTypeAfter(eq(List.of(guildWithoutHistory)), eq(Instant.EPOCH), any(Instant.class)))
            .thenReturn(List.of(
                new GuildMemberEventCountView(guildWithoutHistory, GuildMemberEventType.JOIN, 1L),
                new GuildMemberEventCountView(guildWithoutHistory, GuildMemberEventType.LEAVE, 1L)
            ));

        when(guildMemberService.countPerGuild()).thenReturn(Map.of(guildWithHistory, 20L, guildWithoutHistory, 5L));

        // when
        guildMemberFacade.aggregateGuildMemberStats();

        // then
        List<GuildMemberStat> stats = captureCreatedStats();
        assertThat(stats).hasSize(2);
        GuildMemberStat statWithHistory = stats.stream().filter(s -> s.getGuildId().equals(guildWithHistory)).findFirst().orElseThrow();
        assertThat(statWithHistory.getJoinedMembers()).isEqualTo(1);
        assertThat(statWithHistory.getLeftMembers()).isEqualTo(0);
        assertThat(statWithHistory.getTotalMembers()).isEqualTo(20);
        GuildMemberStat statWithoutHistory = stats.stream().filter(s -> s.getGuildId().equals(guildWithoutHistory)).findFirst().orElseThrow();
        assertThat(statWithoutHistory.getJoinedMembers()).isEqualTo(1);
        assertThat(statWithoutHistory.getLeftMembers()).isEqualTo(1);
        assertThat(statWithoutHistory.getTotalMembers()).isEqualTo(5);
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
        List<GuildMemberStat> stats = captureCreatedStats();
        assertThat(stats).hasSize(2);
        Instant firstCreatedAt = stats.get(0).getCreatedAt();
        assertThat(stats).allMatch(s -> s.getCreatedAt().equals(firstCreatedAt));

        ArgumentCaptor<Instant> queryRunAtCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(guildMemberEventService).countByGuildAndTypeAfter(any(), any(), queryRunAtCaptor.capture());
        assertThat(queryRunAtCaptor.getValue()).isEqualTo(firstCreatedAt);
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

    /**
     * createGuildMemberStats 단일 호출에 전달된 List를 캡처. 배치 insert 검증용.
     */
    @SuppressWarnings("unchecked")
    private List<GuildMemberStat> captureCreatedStats() {
        ArgumentCaptor<List<GuildMemberStat>> captor = ArgumentCaptor.forClass(List.class);
        verify(guildMemberStatService).createGuildMemberStats(captor.capture());
        return captor.getValue();
    }

}
