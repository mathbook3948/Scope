package dev.mathbook3948.scope.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.mathbook3948.scope.domain.guild.GuildService;
import dev.mathbook3948.scope.domain.guild.member.GuildMember;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberEventService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberEventType;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberStatService;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class GuildMemberFacade {

    private final GuildMemberService guildMemberService;

    private final GuildMemberEventService guildMemberEventService;

    private final GuildMemberStatService guildMemberStatService;

    private final GuildService guildService;

    @Transactional
    public void upsertMember(Long guildId, Long memberId, String name, String avatarUrl) {
        guildMemberService.upsertGuildMember(guildId, memberId, name, avatarUrl);
    }

    @Transactional
    public void onGuildMemberJoin(Long guildId, Long memberId, String name, String avatarUrl) {
        guildMemberService.upsertGuildMember(guildId, memberId, name, avatarUrl);
        GuildMember member = guildMemberService.findByGuildIdAndMemberId(guildId, memberId);
        guildMemberEventService.createMemberEvent(member.getGuild(), member, GuildMemberEventType.JOIN);
    }

    @Transactional
    public void onGuildMemberRemove(Long guildId, Long memberId) {
        GuildMember member = guildMemberService.findByGuildIdAndMemberId(guildId, memberId);
        guildMemberEventService.createMemberEvent(member.getGuild(), member, GuildMemberEventType.LEAVE);
        guildMemberService.deleteGuildMember(guildId, memberId);
    }

    /**
     * 모든 길드의 멤버 통계를 집계하여 스냅샷을 생성한다.
     * 마지막 스냅샷 이후 발생한 가입/탈퇴 이벤트를 집계하고, 현재 총 멤버 수와 함께 저장한다.
     */
    @Transactional
    public void aggregateGuildMemberStats() {
        guildService.findAll().forEach(guild -> {
            Long guildId = guild.getGuildId();

            // 마지막 stat 시점 조회
            Instant since = guildMemberStatService.findLatestByGuildId(guildId)
                .map(stat -> stat.getCreatedAt())
                .orElse(Instant.EPOCH);

            // 기준 시점 이후 가입/탈퇴 수 및 현재 총 멤버 수 집계
            int joined = guildMemberEventService.countByGuildIdAndEventTypeAfter(guildId, GuildMemberEventType.JOIN, since);
            int left = guildMemberEventService.countByGuildIdAndEventTypeAfter(guildId, GuildMemberEventType.LEAVE, since);
            int total = guildMemberService.countByGuildId(guildId);

            guildMemberStatService.createGuildMemberStat(guild, joined, left, total);
        });
    }
}
