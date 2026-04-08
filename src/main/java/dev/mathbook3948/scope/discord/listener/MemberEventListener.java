package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.facade.GuildFacade;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberEventListener extends ListenerAdapter {

    private final GuildFacade guildFacade;

    @Override
    public void onReady(ReadyEvent event) {
        event.getJDA().getGuilds().forEach(guild -> {
            guild.loadMembers(member -> {
                if (!member.getUser().isBot()) {
                    guildFacade.upsertMember(guild.getIdLong(), member.getIdLong(), member.getEffectiveName(), member.getEffectiveAvatarUrl());
                }
            });
        });
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        guildFacade.onGuildMemberJoin(event.getGuild().getIdLong(), event.getUser().getIdLong(), event.getUser().getEffectiveName(), event.getUser().getEffectiveAvatarUrl());
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        guildFacade.onGuildMemberRemove(event.getGuild().getIdLong(), event.getUser().getIdLong());
    }

    @Override
    public void onGuildMemberUpdate(GuildMemberUpdateEvent event) {
        guildFacade.upsertMember(event.getGuild().getIdLong(), event.getUser().getIdLong(), event.getMember().getEffectiveName(), event.getMember().getEffectiveAvatarUrl());
    }

}
