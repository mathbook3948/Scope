package dev.mathbook3948.scope.facade;

import dev.mathbook3948.scope.domain.guild.GuildInfo;
import dev.mathbook3948.scope.domain.guild.GuildService;
import dev.mathbook3948.scope.domain.guild.channel.GuildChannelService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberEventService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberStatService;
import dev.mathbook3948.scope.domain.guild.message.GuildMessageEventService;
import dev.mathbook3948.scope.domain.guild.reaction.GuildReactionEventService;
import dev.mathbook3948.scope.domain.guild.thread.GuildThreadEventService;
import dev.mathbook3948.scope.domain.guild.voice.GuildVoiceEventService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildFacade {

    private final GuildService guildService;
    private final GuildMemberService guildMemberService;
    private final GuildMemberEventService guildMemberEventService;
    private final GuildMemberStatService guildMemberStatService;
    private final GuildChannelService guildChannelService;
    private final GuildMessageEventService guildMessageEventService;
    private final GuildReactionEventService guildReactionEventService;
    private final GuildVoiceEventService guildVoiceEventService;
    private final GuildThreadEventService guildThreadEventService;

    @Transactional
    public void upsertGuild(GuildInfo guild) {
        guildService.upsertGuild(guild);
    }

    @Transactional
    public void upsertGuilds(List<GuildInfo> guilds) {
        guildService.upsertGuilds(guilds);
    }

    @Transactional
    public void onGuildLeave(Long guildId) {
        guildMemberStatService.deleteAllByGuildId(guildId);
        guildMemberEventService.deleteAllByGuildId(guildId);
        guildMessageEventService.deleteAllByGuildId(guildId);
        guildReactionEventService.deleteAllByGuildId(guildId);
        guildVoiceEventService.deleteAllByGuildId(guildId);
        guildThreadEventService.deleteAllByGuildId(guildId);
        guildMemberService.deleteAllByGuildId(guildId);
        guildChannelService.deleteAllByGuildId(guildId);
        guildService.deleteGuild(guildId);
    }
}
