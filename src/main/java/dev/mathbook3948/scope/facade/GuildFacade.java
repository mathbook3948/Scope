package dev.mathbook3948.scope.facade;

import dev.mathbook3948.scope.domain.guild.GuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildFacade {

    private final GuildService guildService;

    @Transactional
    public void onGuildJoin(Long guildId, String name) {
        guildService.createGuild(guildId, name);
    }

    @Transactional
    public void onGuildUpdateName(Long guildId, String name) {
        guildService.updateGuild(guildId, name);
    }
}
