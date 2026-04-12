package dev.mathbook3948.scope.domain.guild.voice;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildVoiceStatService {

    private final GuildVoiceStatRepository guildVoiceStatRepository;

    @Transactional
    public void createGuildVoiceStat(GuildVoiceEventInfo info, long duration) {
        guildVoiceStatRepository.save(GuildVoiceStat.of(info, duration));
    }

    @Transactional
    public void deleteAllByGuildId(Long guildId) {
        guildVoiceStatRepository.deleteAllByGuildId(guildId);
    }

    @Transactional
    public void deleteAllBefore(Instant before) {
        guildVoiceStatRepository.deleteAllByCreatedAtBefore(before);
    }
}
