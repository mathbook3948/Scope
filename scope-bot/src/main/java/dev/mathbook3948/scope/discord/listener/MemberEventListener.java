package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.facade.GuildMemberFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberEventListener extends ListenerAdapter {

    private final GuildMemberFacade guildMemberFacade;

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        guildMemberFacade.onGuildMemberJoin(event.getGuild().getIdLong(), event.getMember().getIdLong(), event.getMember().getEffectiveName(), event.getMember().getEffectiveAvatarUrl());
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        guildMemberFacade.onGuildMemberRemove(event.getGuild().getIdLong(), event.getUser().getIdLong());
    }

    @Override
    public void onGuildMemberUpdate(GuildMemberUpdateEvent event) {
        guildMemberFacade.upsertMember(event.getGuild().getIdLong(), event.getMember().getIdLong(), event.getMember().getEffectiveName(), event.getMember().getEffectiveAvatarUrl());
    }
}
