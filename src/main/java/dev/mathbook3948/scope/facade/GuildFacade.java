package dev.mathbook3948.scope.facade;

import dev.mathbook3948.scope.domain.guild.GuildService;
import dev.mathbook3948.scope.domain.guild.channel.GuildChannelService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberEventService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberStatService;
import dev.mathbook3948.scope.domain.guild.message.GuildMessageEventService;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public void upsertGuild(Long guildId, String name) {
        guildService.upsertGuild(guildId, name);
    }

    @Transactional
    public void onGuildUpdateName(Long guildId, String name) {
        guildService.updateGuild(guildId, name);
    }

    @Transactional
    public void onGuildLeave(Long guildId) {
        guildMemberStatService.deleteAllByGuildId(guildId);
        guildMemberEventService.deleteAllByGuildId(guildId);
        guildMessageEventService.deleteAllByGuildId(guildId);
        guildMemberService.deleteAllByGuildId(guildId);
        guildChannelService.deleteAllByGuildId(guildId);
        guildService.deleteGuild(guildId);
    }
}
