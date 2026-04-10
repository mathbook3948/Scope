package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.domain.guild.member.GuildMemberInfo;
import dev.mathbook3948.scope.facade.GuildFacade;
import dev.mathbook3948.scope.facade.GuildMemberFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberEventListener extends ListenerAdapter {

    private final GuildFacade guildFacade;
    private final GuildMemberFacade guildMemberFacade;

    @Override
    public void onReady(ReadyEvent event) {
        event.getJDA().getGuilds().forEach(guild -> {
            guildFacade.upsertGuild(guild.getIdLong(), guild.getName());
            guild.loadMembers().onSuccess(members -> {
                List<GuildMemberInfo> memberInfos = members.stream()
                    .filter(member -> !member.getUser().isBot())
                    .map(member -> new GuildMemberInfo(member.getIdLong(), member.getEffectiveName(), member.getEffectiveAvatarUrl()))
                    .toList();
                guildMemberFacade.upsertMembers(guild.getIdLong(), memberInfos);
            }).onError(e -> log.error("Failed to load members for guild {}", guild.getIdLong(), e));
        });
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        guildMemberFacade.onGuildMemberJoin(event.getGuild().getIdLong(), event.getUser().getIdLong(), event.getUser().getEffectiveName(), event.getUser().getEffectiveAvatarUrl());
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        guildMemberFacade.onGuildMemberRemove(event.getGuild().getIdLong(), event.getUser().getIdLong());
    }

    @Override
    public void onGuildMemberUpdate(GuildMemberUpdateEvent event) {
        guildMemberFacade.upsertMember(event.getGuild().getIdLong(), event.getUser().getIdLong(), event.getMember().getEffectiveName(), event.getMember().getEffectiveAvatarUrl());
    }

}
