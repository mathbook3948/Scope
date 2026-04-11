package dev.mathbook3948.scope.facade;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.mathbook3948.scope.domain.guild.reaction.GuildReactionEventInfo;
import dev.mathbook3948.scope.domain.guild.reaction.GuildReactionEventService;
import dev.mathbook3948.scope.domain.guild.reaction.GuildReactionEventType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildReactionEventFacade {

    private final GuildReactionEventService guildReactionEventService;

    @Transactional
    public void onReactionAdd(GuildReactionEventInfo reaction) {
        guildReactionEventService.createGuildReactionEvent(reaction, GuildReactionEventType.ADD);
    }

    @Transactional
    public void onReactionRemove(GuildReactionEventInfo reaction) {
        guildReactionEventService.createGuildReactionEvent(reaction, GuildReactionEventType.REMOVE);
    }

    /**
     * 보관 기간이 지난 GuildReactionEvent를 일괄 삭제한다.
     *
     * @see dev.mathbook3948.scope.job.GuildReactionEventCleanupJob
     */
    @Transactional
    public void cleanupGuildReactionEvents(Instant before) {
        guildReactionEventService.deleteAllBefore(before);
    }
}
