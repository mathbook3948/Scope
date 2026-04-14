package dev.mathbook3948.scope.domain.guild.message;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildMessageEventService {

    private final GuildMessageEventRepository guildMessageEventRepository;

    public int countByGuildIdAndEventTypeAfter(Long guildId, GuildMessageEventType eventType, Instant after) {
        return guildMessageEventRepository.countByGuildIdAndEventTypeAndCreatedAtAfter(guildId, eventType, after);
    }

    @Transactional
    public void createGuildMessageEvent(GuildMessageEventInfo info, GuildMessageEventType eventType) {
        guildMessageEventRepository.save(GuildMessageEvent.builder()
            .guildId(info.guildId())
            .channelId(info.channelId())
            .memberId(info.memberId())
            .messageId(info.messageId())
            .replyToMessageId(info.replyToMessageId())
            .eventType(eventType)
            .sourceType(info.sourceType())
            .authorType(info.authorType())
            .contentLength(info.contentLength())
            .mentionCount(info.mentionCount())
            .attachmentCount(info.attachmentCount())
            .hasLink(info.hasLink())
            .build());
    }

    @Transactional
    public void createDeleteEvent(GuildMessageEventInfo info) {
        guildMessageEventRepository.save(GuildMessageEvent.builder()
            .guildId(info.guildId())
            .channelId(info.channelId())
            .messageId(info.messageId())
            .eventType(GuildMessageEventType.DELETE)
            .sourceType(info.sourceType())
            .authorType(info.authorType())
            .build());
    }

    @Transactional
    public void deleteAllByGuildId(Long guildId) {
        guildMessageEventRepository.deleteAllByGuildId(guildId);
    }

    @Transactional
    public void deleteAllBefore(Instant before) {
        guildMessageEventRepository.deleteAllByCreatedAtBefore(before);
    }
}
