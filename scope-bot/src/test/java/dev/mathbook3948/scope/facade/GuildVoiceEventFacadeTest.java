package dev.mathbook3948.scope.facade;

import dev.mathbook3948.scope.domain.guild.voice.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuildVoiceEventFacadeTest {

    @InjectMocks
    GuildVoiceEventFacade guildVoiceEventFacade;

    @Mock
    GuildVoiceEventService guildVoiceEventService;

    @Mock
    GuildVoiceStatService guildVoiceStatService;

    private static final Instant LEAVE_AT = Instant.parse("2026-04-14T12:00:00Z");

    @Test
    @DisplayName("LEAVE 시 매칭되는 JOIN 이벤트가 있으면 duration을 계산하여 stat을 저장한다")
    void onVoiceLeave_withMatchingJoinEvent_savesStatWithCorrectDuration() {
        // given
        GuildVoiceEventInfo info = new GuildVoiceEventInfo(1L, 100L, 200L, LEAVE_AT);
        Instant joinedAt = LEAVE_AT.minusSeconds(3600);
        GuildVoiceEvent joinEvent = GuildVoiceEventFixture.create(info, GuildVoiceEventType.JOIN, joinedAt);

        when(guildVoiceEventService.findLatest(1L, 200L))
                .thenReturn(Optional.of(joinEvent));

        // when
        guildVoiceEventFacade.onVoiceLeave(info);

        // then
        ArgumentCaptor<Long> durationCaptor = ArgumentCaptor.forClass(Long.class);
        verify(guildVoiceStatService).createGuildVoiceStat(eq(info), durationCaptor.capture());
        assertThat(durationCaptor.getValue()).isEqualTo(3600L);

        verify(guildVoiceEventService).createGuildVoiceEvent(info, GuildVoiceEventType.LEAVE);
    }

    @Test
    @DisplayName("LEAVE 시 매칭되는 MOVE 이벤트가 있으면 duration을 계산하여 stat을 저장한다")
    void onVoiceLeave_withMatchingMoveEvent_savesStatWithCorrectDuration() {
        // given
        GuildVoiceEventInfo info = new GuildVoiceEventInfo(1L, 100L, 200L, LEAVE_AT);
        Instant movedAt = LEAVE_AT.minusSeconds(1800);
        GuildVoiceEvent moveEvent = GuildVoiceEventFixture.create(info, GuildVoiceEventType.MOVE, movedAt);

        when(guildVoiceEventService.findLatest(1L, 200L))
                .thenReturn(Optional.of(moveEvent));

        // when
        guildVoiceEventFacade.onVoiceLeave(info);

        // then
        ArgumentCaptor<Long> durationCaptor = ArgumentCaptor.forClass(Long.class);
        verify(guildVoiceStatService).createGuildVoiceStat(eq(info), durationCaptor.capture());
        assertThat(durationCaptor.getValue()).isEqualTo(1800L);

        verify(guildVoiceEventService).createGuildVoiceEvent(info, GuildVoiceEventType.LEAVE);
    }

    @Test
    @DisplayName("LEAVE 시 매칭되는 이벤트가 없으면 stat을 저장하지 않는다")
    void onVoiceLeave_noMatchingEvent_doesNotSaveStat() {
        // given
        GuildVoiceEventInfo info = new GuildVoiceEventInfo(1L, 100L, 200L, LEAVE_AT);

        when(guildVoiceEventService.findLatest(1L, 200L))
                .thenReturn(Optional.empty());

        // when
        guildVoiceEventFacade.onVoiceLeave(info);

        // then
        verifyNoInteractions(guildVoiceStatService);
        verify(guildVoiceEventService).createGuildVoiceEvent(info, GuildVoiceEventType.LEAVE);
    }

    @Test
    @DisplayName("LEAVE 시 최신 이벤트가 LEAVE면 중복으로 간주하고 stat을 저장하지 않는다")
    void onVoiceLeave_latestEventIsLeave_doesNotSaveStat() {
        // given
        GuildVoiceEventInfo info = new GuildVoiceEventInfo(1L, 100L, 200L, LEAVE_AT);
        Instant leftAt = LEAVE_AT.minusSeconds(10);
        GuildVoiceEvent leaveEvent = GuildVoiceEventFixture.create(info, GuildVoiceEventType.LEAVE, leftAt);

        when(guildVoiceEventService.findLatest(1L, 200L))
                .thenReturn(Optional.of(leaveEvent));

        // when
        guildVoiceEventFacade.onVoiceLeave(info);

        // then
        verifyNoInteractions(guildVoiceStatService);
        verify(guildVoiceEventService).createGuildVoiceEvent(info, GuildVoiceEventType.LEAVE);
    }

    @Test
    @DisplayName("LEAVE 시 최신 이벤트의 채널이 다르면 stat을 저장하지 않는다")
    void onVoiceLeave_latestEventChannelMismatch_doesNotSaveStat() {
        // given
        GuildVoiceEventInfo info = new GuildVoiceEventInfo(1L, 100L, 200L, LEAVE_AT);
        GuildVoiceEventInfo otherChannelInfo = new GuildVoiceEventInfo(1L, 999L, 200L, LEAVE_AT.minusSeconds(600));
        GuildVoiceEvent joinEvent = GuildVoiceEventFixture.create(otherChannelInfo, GuildVoiceEventType.JOIN, otherChannelInfo.createdAt());

        when(guildVoiceEventService.findLatest(1L, 200L))
                .thenReturn(Optional.of(joinEvent));

        // when
        guildVoiceEventFacade.onVoiceLeave(info);

        // then
        verifyNoInteractions(guildVoiceStatService);
        verify(guildVoiceEventService).createGuildVoiceEvent(info, GuildVoiceEventType.LEAVE);
    }

    @Test
    @DisplayName("MOVE 시 이전 채널의 duration을 계산하여 stat을 저장하고 새 채널로 MOVE 이벤트를 기록한다")
    void onVoiceMove_withMatchingJoinEvent_savesStatAndRecordsMoveEvent() {
        // given
        GuildVoiceEventInfo joinedInfo = new GuildVoiceEventInfo(1L, 200L, 300L, LEAVE_AT);
        GuildVoiceEventInfo leftInfo = new GuildVoiceEventInfo(1L, 100L, 300L, LEAVE_AT);
        Instant joinedAt = LEAVE_AT.minusSeconds(600);
        GuildVoiceEvent joinEvent = GuildVoiceEventFixture.create(leftInfo, GuildVoiceEventType.JOIN, joinedAt);

        when(guildVoiceEventService.findLatest(1L, 300L))
                .thenReturn(Optional.of(joinEvent));

        // when
        guildVoiceEventFacade.onVoiceMove(joinedInfo, leftInfo);

        // then
        ArgumentCaptor<Long> durationCaptor = ArgumentCaptor.forClass(Long.class);
        verify(guildVoiceStatService).createGuildVoiceStat(eq(leftInfo), durationCaptor.capture());
        assertThat(durationCaptor.getValue()).isEqualTo(600L);

        verify(guildVoiceEventService).createGuildVoiceEvent(joinedInfo, GuildVoiceEventType.MOVE);
    }

    @Test
    @DisplayName("MOVE 시 매칭되는 이벤트가 없으면 MOVE 이벤트만 기록한다")
    void onVoiceMove_noMatchingEvent_onlyRecordsMoveEvent() {
        // given
        GuildVoiceEventInfo joinedInfo = new GuildVoiceEventInfo(1L, 200L, 300L, LEAVE_AT);
        GuildVoiceEventInfo leftInfo = new GuildVoiceEventInfo(1L, 100L, 300L, LEAVE_AT);

        when(guildVoiceEventService.findLatest(1L, 300L))
                .thenReturn(Optional.empty());

        // when
        guildVoiceEventFacade.onVoiceMove(joinedInfo, leftInfo);

        // then
        verifyNoInteractions(guildVoiceStatService);
        verify(guildVoiceEventService).createGuildVoiceEvent(joinedInfo, GuildVoiceEventType.MOVE);
    }

    @Test
    @DisplayName("MOVE 시 최신 이벤트가 LEAVE면 stat을 저장하지 않고 MOVE 이벤트만 기록한다")
    void onVoiceMove_latestEventIsLeave_onlyRecordsMoveEvent() {
        // given
        GuildVoiceEventInfo joinedInfo = new GuildVoiceEventInfo(1L, 200L, 300L, LEAVE_AT);
        GuildVoiceEventInfo leftInfo = new GuildVoiceEventInfo(1L, 100L, 300L, LEAVE_AT);
        Instant leftAt = LEAVE_AT.minusSeconds(10);
        GuildVoiceEvent leaveEvent = GuildVoiceEventFixture.create(leftInfo, GuildVoiceEventType.LEAVE, leftAt);

        when(guildVoiceEventService.findLatest(1L, 300L))
                .thenReturn(Optional.of(leaveEvent));

        // when
        guildVoiceEventFacade.onVoiceMove(joinedInfo, leftInfo);

        // then
        verifyNoInteractions(guildVoiceStatService);
        verify(guildVoiceEventService).createGuildVoiceEvent(joinedInfo, GuildVoiceEventType.MOVE);
    }

    @Test
    @DisplayName("MOVE 시 최신 이벤트의 채널이 다르면 stat을 저장하지 않고 MOVE 이벤트만 기록한다")
    void onVoiceMove_latestEventChannelMismatch_onlyRecordsMoveEvent() {
        // given
        GuildVoiceEventInfo joinedInfo = new GuildVoiceEventInfo(1L, 200L, 300L, LEAVE_AT);
        GuildVoiceEventInfo leftInfo = new GuildVoiceEventInfo(1L, 100L, 300L, LEAVE_AT);
        GuildVoiceEventInfo otherChannelInfo = new GuildVoiceEventInfo(1L, 999L, 300L, LEAVE_AT.minusSeconds(600));
        GuildVoiceEvent joinEvent = GuildVoiceEventFixture.create(otherChannelInfo, GuildVoiceEventType.JOIN, otherChannelInfo.createdAt());

        when(guildVoiceEventService.findLatest(1L, 300L))
                .thenReturn(Optional.of(joinEvent));

        // when
        guildVoiceEventFacade.onVoiceMove(joinedInfo, leftInfo);

        // then
        verifyNoInteractions(guildVoiceStatService);
        verify(guildVoiceEventService).createGuildVoiceEvent(joinedInfo, GuildVoiceEventType.MOVE);
    }

    @Test
    @DisplayName("JOIN 시 이벤트만 기록한다")
    void onVoiceJoin_recordsJoinEvent() {
        // given
        GuildVoiceEventInfo info = new GuildVoiceEventInfo(1L, 100L, 200L, LEAVE_AT);

        // when
        guildVoiceEventFacade.onVoiceJoin(info);

        // then
        verify(guildVoiceEventService).createGuildVoiceEvent(info, GuildVoiceEventType.JOIN);
        verifyNoInteractions(guildVoiceStatService);
    }
}
