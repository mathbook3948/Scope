package dev.mathbook3948.scope.facade;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.mathbook3948.scope.domain.guild.message.GuildMessageEventInfo;
import dev.mathbook3948.scope.domain.guild.message.GuildMessageEventService;
import dev.mathbook3948.scope.domain.guild.message.GuildMessageEventType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildMessageEventFacade {

    private final GuildMessageEventService guildMessageEventService;

    @Transactional
    public void onMessageSend(GuildMessageEventInfo info) {
        guildMessageEventService.createGuildMessageEvent(info, GuildMessageEventType.SEND);
    }

    @Transactional
    public void onMessageUpdate(GuildMessageEventInfo info) {
        guildMessageEventService.createGuildMessageEvent(info, GuildMessageEventType.UPDATE);
    }

    @Transactional
    public void onMessageDelete(GuildMessageEventInfo info) {
        guildMessageEventService.createDeleteEvent(info);
    }

    /**
     * 보관 기간이 지난 GuildMessageEvent를 일괄 삭제한다.
     *
     * @see dev.mathbook3948.scope.job.GuildMessageEventCleanupJob
     */
    @Transactional
    public void cleanupGuildMessageEvents(Instant before) {
        guildMessageEventService.deleteAllBefore(before);
    }
}
