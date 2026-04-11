package dev.mathbook3948.scope.facade;

import dev.mathbook3948.scope.domain.guild.GuildService;
import dev.mathbook3948.scope.domain.guild.member.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;

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
    @DisplayName("스냅샷이 한 번도 없으면 모든 이벤트를 카운트한다")
    void aggregateGuildMemberStats_noStatHistory_countsAllEvents() {
        // given
        Long guildId = 1L;

        when(guildService.findAllGuildIds()).thenReturn(List.of(guildId));
        when(guildMemberStatService.findLatestCreatedAtPerGuild()).thenReturn(Map.of());

        when(guildMemberEventService.findAllAfter(Instant.EPOCH)).thenReturn(List.of(
            GuildMemberEventFixture.create(guildId, 101L, GuildMemberEventType.JOIN, Instant.parse("2026-04-01T00:00:00Z")),
            GuildMemberEventFixture.create(guildId, 102L, GuildMemberEventType.JOIN, Instant.parse("2026-04-05T00:00:00Z")),
            GuildMemberEventFixture.create(guildId, 103L, GuildMemberEventType.LEAVE, Instant.parse("2026-04-06T01:20:00Z"))
        ));

        when(guildMemberService.countPerGuild()).thenReturn(Map.of(guildId, 1L));

        // when
        guildMemberFacade.aggregateGuildMemberStats();

        // then
        verify(guildMemberStatService).createGuildMemberStat(guildId, 2, 1, 1);
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
        when(guildMemberEventService.findAllAfter(lastStatAt)).thenReturn(List.of());
        when(guildMemberService.countPerGuild()).thenReturn(Map.of(guildId, 10L));

        // when
        guildMemberFacade.aggregateGuildMemberStats();

        // then
        verify(guildMemberStatService).createGuildMemberStat(guildId, 0, 0, 10);
        verifyNoMoreInteractions(guildMemberStatService, guildMemberEventService, guildMemberService);
    }

    @Test
    @DisplayName("스냅샷 이력이 있는 길드와 없는 길드가 섞여 있으면 각각의 기준 시점으로 필터링한다")
    void aggregateGuildMemberStats_mixedStatHistory_filtersPerGuild() {
        // given
        Long guildWithHistory = 1L;
        Long guildWithoutHistory = 2L;
        Instant lastStatAt = Instant.parse("2026-04-10T00:00:00Z");

        when(guildService.findAllGuildIds()).thenReturn(List.of(guildWithHistory, guildWithoutHistory));
        when(guildMemberStatService.findLatestCreatedAtPerGuild()).thenReturn(Map.of(guildWithHistory, lastStatAt));

        // globalSince = EPOCH (guildWithoutHistory에 이력이 없으므로)
        // guildWithHistory: lastStatAt 이후 이벤트만 카운트
        // guildWithoutHistory: EPOCH 이후 = 모든 이벤트 카운트
        when(guildMemberEventService.findAllAfter(Instant.EPOCH)).thenReturn(List.of(
            GuildMemberEventFixture.create(guildWithHistory, 101L, GuildMemberEventType.JOIN, lastStatAt.minusSeconds(60)), // 무시
            GuildMemberEventFixture.create(guildWithHistory, 102L, GuildMemberEventType.JOIN, lastStatAt.plusSeconds(60)), // 카운트
            GuildMemberEventFixture.create(guildWithoutHistory, 201L, GuildMemberEventType.JOIN, Instant.parse("2026-01-01T00:00:00Z")), // 카운트
            GuildMemberEventFixture.create(guildWithoutHistory, 202L, GuildMemberEventType.LEAVE, Instant.parse("2026-03-15T00:00:00Z")) // 카운트
        ));

        when(guildMemberService.countPerGuild()).thenReturn(Map.of(guildWithHistory, 20L, guildWithoutHistory, 5L));

        // when
        guildMemberFacade.aggregateGuildMemberStats();

        // then
        verify(guildMemberStatService).createGuildMemberStat(guildWithHistory, 1, 0, 20);
        verify(guildMemberStatService).createGuildMemberStat(guildWithoutHistory, 1, 1, 5);
        verifyNoMoreInteractions(guildMemberStatService, guildMemberEventService, guildMemberService);
    }

    @Test
    @DisplayName("마지막 스냅샷과 동일한 시각의 이벤트는 집계에서 제외한다")
    void aggregateGuildMemberStats_eventAtExactSnapshotTime_excluded() {
        // given
        Long guildId = 1L;
        Instant lastStatAt = Instant.parse("2026-04-10T00:00:00Z");

        when(guildService.findAllGuildIds()).thenReturn(List.of(guildId));
        when(guildMemberStatService.findLatestCreatedAtPerGuild()).thenReturn(Map.of(guildId, lastStatAt));

        when(guildMemberEventService.findAllAfter(lastStatAt)).thenReturn(List.of(
            GuildMemberEventFixture.create(guildId, 101L, GuildMemberEventType.JOIN, lastStatAt) // 정확히 같은 시각 → 제외
        ));

        when(guildMemberService.countPerGuild()).thenReturn(Map.of(guildId, 5L));

        // when
        guildMemberFacade.aggregateGuildMemberStats();

        // then
        verify(guildMemberStatService).createGuildMemberStat(guildId, 0, 0, 5);
        verifyNoMoreInteractions(guildMemberStatService, guildMemberEventService, guildMemberService);
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
}
