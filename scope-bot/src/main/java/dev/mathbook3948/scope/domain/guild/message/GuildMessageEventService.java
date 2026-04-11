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
    public void createGuildMessageEvent(Long guildId, Long channelId, Long memberId, Long messageId,
                                    GuildMessageEventType eventType, Integer contentLength) {
        guildMessageEventRepository.save(GuildMessageEvent.of(guildId, channelId, memberId, messageId, eventType, contentLength));
    }

    @Transactional
    public void createDeleteEvent(Long guildId, Long channelId, Long messageId) {
        guildMessageEventRepository.save(GuildMessageEvent.ofDelete(guildId, channelId, messageId));
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
