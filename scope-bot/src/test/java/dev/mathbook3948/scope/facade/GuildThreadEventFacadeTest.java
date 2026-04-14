package dev.mathbook3948.scope.facade;

import dev.mathbook3948.scope.domain.guild.thread.GuildThreadEventInfo;
import dev.mathbook3948.scope.domain.guild.thread.GuildThreadEventService;
import dev.mathbook3948.scope.domain.guild.thread.GuildThreadEventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class GuildThreadEventFacadeTest {

    @InjectMocks
    GuildThreadEventFacade guildThreadEventFacade;

    @Mock
    GuildThreadEventService guildThreadEventService;

    @Test
    @DisplayName("onThreadCreate는 CREATE 타입으로 Service에 위임한다")
    void onThreadCreate_threadInfo_delegatesToServiceWithCreateType() {
        // given
        GuildThreadEventInfo info = new GuildThreadEventInfo(1L, 50L, 300L, 777L, "sprint");

        // when
        guildThreadEventFacade.onThreadCreate(info);

        // then
        verify(guildThreadEventService).createGuildThreadEvent(info, GuildThreadEventType.CREATE);
        verifyNoMoreInteractions(guildThreadEventService);
    }

    @Test
    @DisplayName("onThreadDelete는 DELETE 타입으로 Service에 위임한다")
    void onThreadDelete_threadInfo_delegatesToServiceWithDeleteType() {
        // given
        GuildThreadEventInfo info = new GuildThreadEventInfo(1L, 50L, 300L, 777L, "sprint");

        // when
        guildThreadEventFacade.onThreadDelete(info);

        // then
        verify(guildThreadEventService).createGuildThreadEvent(info, GuildThreadEventType.DELETE);
        verifyNoMoreInteractions(guildThreadEventService);
    }

    @Test
    @DisplayName("cleanupGuildThreadEvents는 지정 시각 이전 이벤트를 삭제하도록 위임한다")
    void cleanupGuildThreadEvents_cutoffInstant_delegatesToService() {
        // given
        Instant cutoff = Instant.parse("2026-01-01T00:00:00Z");

        // when
        guildThreadEventFacade.cleanupGuildThreadEvents(cutoff);

        // then
        verify(guildThreadEventService).deleteAllBefore(cutoff);
        verifyNoMoreInteractions(guildThreadEventService);
    }
}
