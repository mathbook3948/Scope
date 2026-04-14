package dev.mathbook3948.scope.domain.guild.voice;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildVoiceEventService {

    private final GuildVoiceEventRepository guildVoiceEventRepository;

    @Transactional
    public void createGuildVoiceEvent(GuildVoiceEventInfo info, GuildVoiceEventType eventType) {
        guildVoiceEventRepository.save(GuildVoiceEvent.builder()
            .guildId(info.guildId())
            .channelId(info.channelId())
            .memberId(info.memberId())
            .eventType(eventType)
            .createdAt(info.createdAt())
            .build());
    }

    @Transactional
    public void deleteAllByGuildId(Long guildId) {
        guildVoiceEventRepository.deleteAllByGuildId(guildId);
    }

    @Transactional
    public void deleteAllBefore(Instant before) {
        guildVoiceEventRepository.deleteAllByCreatedAtBefore(before);
    }

    public Optional<GuildVoiceEvent> findLatest(Long guildId, Long memberId) {
        return guildVoiceEventRepository.findLatest(guildId, memberId);
    }
}
