package dev.mathbook3948.scope.facade;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.mathbook3948.scope.domain.guild.message.GuildMessageEventService;
import dev.mathbook3948.scope.domain.guild.message.GuildMessageEventType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildMessageEventFacade {

    private final GuildMessageEventService guildMessageEventService;

    @Transactional
    public void onMessageSend(Long guildId, Long channelId, Long memberId, Long messageId, Integer contentLength) {
        guildMessageEventService.createGuildMessageEvent(guildId, channelId, memberId, messageId, GuildMessageEventType.SEND, contentLength);
    }

    @Transactional
    public void onMessageUpdate(Long guildId, Long channelId, Long memberId, Long messageId, Integer contentLength) {
        guildMessageEventService.createGuildMessageEvent(guildId, channelId, memberId, messageId, GuildMessageEventType.UPDATE, contentLength);
    }

    @Transactional
    public void onMessageDelete(Long guildId, Long channelId, Long messageId) {
        guildMessageEventService.createDeleteEvent(guildId, channelId, messageId);
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
