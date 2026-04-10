package dev.mathbook3948.scope.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.mathbook3948.scope.domain.guild.message.MessageEventService;
import dev.mathbook3948.scope.domain.guild.message.MessageEventType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageEventFacade {

    private final MessageEventService messageEventService;

    @Transactional
    public void onMessageSend(Long guildId, Long channelId, Long memberId, Long messageId, Integer contentLength) {
        messageEventService.createMessageEvent(guildId, channelId, memberId, messageId, MessageEventType.SEND, contentLength);
    }

    @Transactional
    public void onMessageUpdate(Long guildId, Long channelId, Long memberId, Long messageId, Integer contentLength) {
        messageEventService.createMessageEvent(guildId, channelId, memberId, messageId, MessageEventType.UPDATE, contentLength);
    }

    @Transactional
    public void onMessageDelete(Long guildId, Long channelId, Long messageId) {
        messageEventService.createDeleteEvent(guildId, channelId, messageId);
    }
}
