package dev.mathbook3948.scope.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.mathbook3948.scope.domain.guild.Guild;
import dev.mathbook3948.scope.domain.guild.GuildService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberEventService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberEventType;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberInfo;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberStatService;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void upsertMembers(Long guildId, List<GuildMemberInfo> members) {
        guildMemberService.upsertGuildMembers(guildId, members);
    }

    @Transactional
    public void onGuildMemberJoin(Long guildId, Long memberId, String name, String avatarUrl) {
        guildMemberService.upsertGuildMember(guildId, memberId, name, avatarUrl);
        guildMemberEventService.createMemberEvent(guildId, memberId, GuildMemberEventType.JOIN);
    }

    @Transactional
    public void onGuildMemberRemove(Long guildId, Long memberId) {
        guildMemberEventService.createMemberEvent(guildId, memberId, GuildMemberEventType.LEAVE);
        guildMemberService.deleteGuildMember(guildId, memberId);
    }

    /**
     * 모든 길드의 멤버 통계를 집계하여 스냅샷을 생성한다.
     * 마지막 스냅샷 이후 발생한 가입/탈퇴 이벤트를 집계하고, 현재 총 멤버 수와 함께 저장한다.
     */
    @Transactional
    public void aggregateGuildMemberStats() {
        List<Guild> guilds = guildService.findAll();

        // 길드별 마지막 stat 시점
        Map<Long, Instant> latestStatAt = guildMemberStatService.findLatestCreatedAtPerGuild();
        Instant globalSince = latestStatAt.values().stream().min(Instant::compareTo).orElse(Instant.EPOCH);

        // globalSince 이후 이벤트를 한 번에 조회 후, 길드별 since로 필터링하여 집계
        Map<Long, Map<GuildMemberEventType, Integer>> eventCounts = new HashMap<>();
        for (var event : guildMemberEventService.findAllAfter(globalSince)) {
            Instant since = latestStatAt.getOrDefault(event.getGuildId(), Instant.EPOCH);
            if (!event.getCreatedAt().isAfter(since)) continue;

            eventCounts
                .computeIfAbsent(event.getGuildId(), k -> new HashMap<>())
                .merge(event.getEventType(), 1, Integer::sum);
        }

        // 길드별 현재 총 멤버 수
        Map<Long, Long> totalMembers = guildMemberService.countPerGuild();

        // 각 길드별로 stat insert
        for (Guild guild : guilds) {
            Long guildId = guild.getGuildId();

            Map<GuildMemberEventType, Integer> counts = eventCounts.getOrDefault(guildId, Map.of());
            int joined = counts.getOrDefault(GuildMemberEventType.JOIN, 0);
            int left = counts.getOrDefault(GuildMemberEventType.LEAVE, 0);
            int total = totalMembers.getOrDefault(guildId, 0L).intValue();

            guildMemberStatService.createGuildMemberStat(guild, joined, left, total);
        }
    }
}
