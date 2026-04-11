package dev.mathbook3948.scope.facade;

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
}
