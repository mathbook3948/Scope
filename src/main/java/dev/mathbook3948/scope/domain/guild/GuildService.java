package dev.mathbook3948.scope.domain.guild;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildService {

    private final GuildRepository guildRepository;

    @Transactional
    public void createGuild(Long guildId, String name) {
        guildRepository.save(Guild.of(guildId, name));
    }

    @Transactional
    public void updateGuild(Long guildId, String name) {
        guildRepository.findById(guildId)
            .ifPresent(guild -> guild.updateName(name));
    }
}
