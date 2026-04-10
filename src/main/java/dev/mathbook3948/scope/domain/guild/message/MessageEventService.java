package dev.mathbook3948.scope.domain.guild.message;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageEventService {

    private final MessageEventRepository messageEventRepository;

    public int countByGuildIdAndEventTypeAfter(Long guildId, MessageEventType eventType, Instant after) {
        return messageEventRepository.countByGuildIdAndEventTypeAndCreatedAtAfter(guildId, eventType, after);
    }

    @Transactional
    public void createMessageEvent(Long guildId, Long channelId, Long memberId, Long messageId,
                                    MessageEventType eventType, Integer contentLength) {
        messageEventRepository.save(MessageEvent.of(guildId, channelId, memberId, messageId, eventType, contentLength));
    }

    @Transactional
    public void createDeleteEvent(Long guildId, Long channelId, Long messageId) {
        messageEventRepository.save(MessageEvent.ofDelete(guildId, channelId, messageId));
    }

    @Transactional
    public void deleteAllByGuildId(Long guildId) {
        messageEventRepository.deleteAllByGuildId(guildId);
    }
}
