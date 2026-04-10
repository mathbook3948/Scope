package dev.mathbook3948.scope.domain.guild;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildService {

    private final GuildRepository guildRepository;

    public List<Guild> findAll() {
        return guildRepository.findAll();
    }

    public Guild getReferenceById(Long guildId) {
        return guildRepository.getReferenceById(guildId);
    }

    @Transactional
    public void createGuild(Long guildId, String name) {
        guildRepository.save(Guild.of(guildId, name));
    }

    @Transactional
    public void upsertGuild(GuildInfo guild) {
        guildRepository.findById(guild.guildId())
            .ifPresentOrElse(
                g -> g.updateName(guild.name()),
                () -> guildRepository.save(Guild.of(guild.guildId(), guild.name()))
            );
    }

    @Transactional
    public void upsertGuilds(List<GuildInfo> guilds) {
        List<Long> guildIds = guilds.stream().map(GuildInfo::guildId).toList();

        Map<Long, Guild> existingMap = guildRepository.findAllById(guildIds)
            .stream()
            .collect(Collectors.toMap(Guild::getGuildId, Function.identity()));

        List<Guild> newGuilds = new ArrayList<>();

        for (GuildInfo info : guilds) {
            Guild existing = existingMap.get(info.guildId());
            if (existing != null) {
                if (!existing.getName().equals(info.name())) {
                    existing.updateName(info.name());
                }
            } else {
                newGuilds.add(Guild.of(info.guildId(), info.name()));
            }
        }

        if (!newGuilds.isEmpty()) {
            guildRepository.saveAll(newGuilds);
        }
    }

    @Transactional
    public void deleteGuild(Long guildId) {
        guildRepository.deleteById(guildId);
    }

    /**
     * Guild 명을 업데이트한다
     * @param guildId Guild ID
     * @param name Guild 명
     * 
     * @throws IllegalStateException Guild ID에 해당하는 Guild가 존재하지 않을경우
     */
    @Transactional
    public void updateGuild(Long guildId, String name) {
        Guild guild = guildRepository.findById(guildId).orElseThrow(() -> new IllegalStateException("Guild not found: " + guildId));
        guild.updateName(name);
    }
}
