package dev.mathbook3948.scope.domain.guild;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public List<Long> findAllGuildIds() {
        return guildRepository.findAllGuildIds();
    }

    @Transactional
    public void createGuild(Long guildId, String name, String iconUrl) {
        guildRepository.save(Guild.of(guildId, name, iconUrl));
    }

    @Transactional
    public void upsertGuild(GuildInfo guild) {
        guildRepository.findById(guild.guildId())
            .ifPresentOrElse(
                g -> {
                    g.updateName(guild.name());
                    g.updateIconUrl(guild.iconUrl());
                },
                () -> guildRepository.save(Guild.of(guild.guildId(), guild.name(), guild.iconUrl()))
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
                if (!Objects.equals(existing.getIconUrl(), info.iconUrl())) {
                    existing.updateIconUrl(info.iconUrl());
                }
            } else {
                newGuilds.add(Guild.of(info.guildId(), info.name(), info.iconUrl()));
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
}
