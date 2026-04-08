package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.facade.GuildFacade;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GuildEventListener extends ListenerAdapter {

    private final GuildFacade guildFacade;

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        guildFacade.onGuildJoin(event.getGuild().getIdLong(), event.getGuild().getName());
    }

    @Override
    public void onGuildUpdateName(GuildUpdateNameEvent event) {
        guildFacade.onGuildUpdateName(event.getGuild().getIdLong(), event.getGuild().getName());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        guildFacade.onGuildLeave(event.getGuild().getIdLong());
    }
}
