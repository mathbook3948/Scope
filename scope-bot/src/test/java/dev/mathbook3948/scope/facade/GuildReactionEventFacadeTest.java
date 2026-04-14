package dev.mathbook3948.scope.facade;

import dev.mathbook3948.scope.domain.guild.AuthorType;
import dev.mathbook3948.scope.domain.guild.reaction.GuildReactionEventInfo;
import dev.mathbook3948.scope.domain.guild.reaction.GuildReactionEventService;
import dev.mathbook3948.scope.domain.guild.reaction.GuildReactionEventType;
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
class GuildReactionEventFacadeTest {

    @InjectMocks
    GuildReactionEventFacade guildReactionEventFacade;

    @Mock
    GuildReactionEventService guildReactionEventService;

    @Test
    @DisplayName("onReactionAdd는 ADD 타입으로 Service에 위임한다")
    void onReactionAdd_reactionInfo_delegatesToServiceWithAddType() {
        // given
        GuildReactionEventInfo info = new GuildReactionEventInfo(1L, 10L, 100L, 200L, "smile", AuthorType.USER);

        // when
        guildReactionEventFacade.onReactionAdd(info);

        // then
        verify(guildReactionEventService).createGuildReactionEvent(info, GuildReactionEventType.ADD);
        verifyNoMoreInteractions(guildReactionEventService);
    }

    @Test
    @DisplayName("onReactionRemove는 REMOVE 타입으로 Service에 위임한다")
    void onReactionRemove_reactionInfo_delegatesToServiceWithRemoveType() {
        // given
        GuildReactionEventInfo info = new GuildReactionEventInfo(1L, 10L, 100L, 200L, "heart", AuthorType.USER);

        // when
        guildReactionEventFacade.onReactionRemove(info);

        // then
        verify(guildReactionEventService).createGuildReactionEvent(info, GuildReactionEventType.REMOVE);
        verifyNoMoreInteractions(guildReactionEventService);
    }

    @Test
    @DisplayName("cleanupGuildReactionEvents는 지정 시각 이전 이벤트를 삭제하도록 위임한다")
    void cleanupGuildReactionEvents_cutoffInstant_delegatesToService() {
        // given
        Instant cutoff = Instant.parse("2026-01-01T00:00:00Z");

        // when
        guildReactionEventFacade.cleanupGuildReactionEvents(cutoff);

        // then
        verify(guildReactionEventService).deleteAllBefore(cutoff);
        verifyNoMoreInteractions(guildReactionEventService);
    }
}
