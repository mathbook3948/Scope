package dev.mathbook3948.scope.domain.guild.member;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildMemberStatService {

    private final GuildMemberStatRepository guildMemberStatRepository;

    public Optional<GuildMemberStat> findLatestByGuildId(Long guildId) {
        return guildMemberStatRepository.findTopByGuildIdOrderByCreatedAtDesc(guildId);
    }

    public Map<Long, Instant> findLatestCreatedAtPerGuild() {
        Map<Long, Instant> result = new HashMap<>();
        for (Object[] row : guildMemberStatRepository.findLatestCreatedAtPerGuild()) {
            result.put((Long) row[0], (Instant) row[1]);
        }
        return result;
    }

    @Transactional
    public void deleteAllByGuildId(Long guildId) {
        guildMemberStatRepository.deleteAllByGuildId(guildId);
    }

    @Transactional
    public void deleteAllBefore(Instant before) {
        guildMemberStatRepository.deleteAllByCreatedAtBefore(before);
    }

    @Transactional
    public void createGuildMemberStats(List<GuildMemberStat> stats) {
        guildMemberStatRepository.saveAll(stats);
    }
}
