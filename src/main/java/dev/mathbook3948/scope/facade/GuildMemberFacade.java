package dev.mathbook3948.scope.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.mathbook3948.scope.domain.guild.member.GuildMemberEventService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuildMemberFacade {

    private final GuildMemberService guildMemberService;

    private final GuildMemberEventService guildMemberEventService;

    @Transactional
    public void upsertMember(Long guildId, Long memberId, String name, String avatarUrl) {
        guildMemberService.upsertGuildMember(guildId, memberId, name, avatarUrl);
    }

    @Transactional
    public void onGuildMemberJoin(Long guildId, Long memberId, String name, String avatarUrl) {
        guildMemberService.upsertGuildMember(guildId, memberId, name, avatarUrl);
        guildMemberEventService.createMemberJoinEvent(guildId, memberId);
    }

    @Transactional
    public void onGuildMemberRemove(Long guildId, Long memberId) {
        guildMemberService.deleteGuildMember(guildId, memberId);
        guildMemberEventService.createMemberLeaveEvent(guildId, memberId);
    }
}
