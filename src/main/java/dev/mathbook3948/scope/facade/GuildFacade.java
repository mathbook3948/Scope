package dev.mathbook3948.scope.facade;

import dev.mathbook3948.scope.domain.guild.GuildService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberEventService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberService;
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

    @Transactional
    public void onGuildJoin(Long guildId, String name) {
        guildService.createGuild(guildId, name);
    }

    @Transactional
    public void onGuildUpdateName(Long guildId, String name) {
        guildService.updateGuild(guildId, name);
    }

    @Transactional
    public void upsertMember(Long guildId, Long memberId, String name, String avatarUrl) {
        guildMemberService.createGuildMember(guildId, memberId, name, avatarUrl);
    }

    @Transactional
    public void onGuildMemberJoin(Long guildId, Long memberId, String name, String avatarUrl) {
        guildMemberService.createGuildMember(guildId, memberId, name, avatarUrl);
        guildMemberEventService.createMemberJoinEvent(guildId, memberId);
    }

    @Transactional
    public void onGuildMemberRemove(Long guildId, Long memberId) {
        guildMemberService.deleteGuildMember(guildId, memberId);
        guildMemberEventService.createMemberLeaveEvent(guildId, memberId);
    }
}
