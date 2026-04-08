package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.facade.GuildFacade;
import dev.mathbook3948.scope.facade.GuildMemberFacade;
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

    private final GuildMemberFacade guildMemberFacade;

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        guildFacade.onGuildJoin(event.getGuild().getIdLong(), event.getGuild().getName());

        event.getGuild().loadMembers(member -> {
            if (!member.getUser().isBot()) {
                guildMemberFacade.upsertMember(event.getGuild().getIdLong(), member.getIdLong(), member.getEffectiveName(), member.getEffectiveAvatarUrl());
            }
        });
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
