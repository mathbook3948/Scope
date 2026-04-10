package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.domain.guild.GuildInfo;
import dev.mathbook3948.scope.domain.guild.channel.GuildChannelInfo;
import dev.mathbook3948.scope.domain.guild.channel.GuildChannelType;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberInfo;
import dev.mathbook3948.scope.facade.GuildChannelFacade;
import dev.mathbook3948.scope.facade.GuildFacade;
import dev.mathbook3948.scope.facade.GuildMemberFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GuildEventListener extends ListenerAdapter {

    private final GuildFacade guildFacade;
    private final GuildMemberFacade guildMemberFacade;
    private final GuildChannelFacade guildChannelFacade;

    @Override
    public void onReady(ReadyEvent event) {

        //guild만 upsert
        List<GuildInfo> guildInfos = event.getJDA().getGuilds().stream()
            .map(guild -> new GuildInfo(guild.getIdLong(), guild.getName()))
            .toList();

        guildFacade.upsertGuilds(guildInfos);
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        guildFacade.upsertGuild(new GuildInfo(event.getGuild().getIdLong(), event.getGuild().getName()));

        List<GuildChannelInfo> channelInfos = event.getGuild().getChannels().stream()
            .map(ch -> new GuildChannelInfo(ch.getIdLong(), ch.getName(), GuildChannelType.from(ch.getType().name())))
            .toList();
        guildChannelFacade.upsertChannels(event.getGuild().getIdLong(), channelInfos);

        event.getGuild().loadMembers().onSuccess(members -> {
            List<GuildMemberInfo> guildMemberInfos = members.stream()
                .filter(member -> !member.getUser().isBot())
                .map(member -> new GuildMemberInfo(member.getIdLong(), member.getEffectiveName(), member.getEffectiveAvatarUrl()))
                .toList();
            guildMemberFacade.upsertMembers(event.getGuild().getIdLong(), guildMemberInfos);
        }).onError(e -> log.error("Failed to load members for guild {}", event.getGuild().getIdLong(), e));
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
