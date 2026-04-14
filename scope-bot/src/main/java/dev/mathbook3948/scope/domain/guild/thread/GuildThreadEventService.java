package dev.mathbook3948.scope.domain.guild.thread;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildThreadEventService {

    private final GuildThreadEventRepository guildThreadEventRepository;

    @Transactional
    public void createGuildThreadEvent(GuildThreadEventInfo info, GuildThreadEventType eventType) {
        guildThreadEventRepository.save(GuildThreadEvent.builder()
            .guildId(info.guildId())
            .parentChannelId(info.parentChannelId())
            .threadId(info.threadId())
            .ownerId(info.ownerId())
            .name(info.name())
            .eventType(eventType)
            .build());
    }

    @Transactional
    public void deleteAllByGuildId(Long guildId) {
        guildThreadEventRepository.deleteAllByGuildId(guildId);
    }

    @Transactional
    public void deleteAllBefore(Instant before) {
        guildThreadEventRepository.deleteAllByCreatedAtBefore(before);
    }
}
