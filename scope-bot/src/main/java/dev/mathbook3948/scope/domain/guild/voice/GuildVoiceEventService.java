package dev.mathbook3948.scope.domain.guild.voice;

import java.time.Instant;

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
        guildVoiceEventRepository.save(GuildVoiceEvent.of(info, eventType));
    }

    @Transactional
    public void deleteAllByGuildId(Long guildId) {
        guildVoiceEventRepository.deleteAllByGuildId(guildId);
    }

    @Transactional
    public void deleteAllBefore(Instant before) {
        guildVoiceEventRepository.deleteAllByCreatedAtBefore(before);
    }
}
