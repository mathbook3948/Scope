package dev.mathbook3948.scope.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.mathbook3948.scope.domain.guild.GuildService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberEventService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberEventType;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberInfo;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberStat;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberStatService;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildMemberFacade {

    private final GuildMemberService guildMemberService;

    private final GuildMemberEventService guildMemberEventService;

    private final GuildMemberStatService guildMemberStatService;

    private final GuildService guildService;

    @Transactional
    public void upsertMember(Long guildId, GuildMemberInfo info) {
        guildMemberService.upsertGuildMember(guildId, info);
    }

    @Transactional
    public void upsertMembers(Long guildId, List<GuildMemberInfo> members) {
        guildMemberService.upsertGuildMembers(guildId, members);
    }

    @Transactional
    public void onGuildMemberJoin(Long guildId, GuildMemberInfo info) {
        guildMemberService.upsertGuildMember(guildId, info);
        guildMemberEventService.createMemberEvent(guildId, info.memberId(), GuildMemberEventType.JOIN);
    }

    @Transactional
    public void onGuildMemberRemove(Long guildId, Long memberId) {
        guildMemberEventService.createMemberEvent(guildId, memberId, GuildMemberEventType.LEAVE);
        guildMemberService.deleteGuildMember(guildId, memberId);
    }

    /**
     * 보관 기간이 지난 GuildMemberEvent를 일괄 삭제한다.
     *
     * @see dev.mathbook3948.scope.job.GuildMemberEventCleanupJob
     */
    @Transactional
    public void cleanupGuildMemberEvents(Instant before) {
        guildMemberEventService.deleteAllBefore(before);
    }

    /**
     * 보관 기간이 지난 GuildMemberStat을 일괄 삭제한다.
     *
     * @see dev.mathbook3948.scope.job.GuildMemberStatCleanupJob
     */
    @Transactional
    public void cleanupGuildMemberStats(Instant before) {
        guildMemberStatService.deleteAllBefore(before);
    }

    /**
     * 모든 길드의 멤버 통계를 집계하여 스냅샷을 생성한다.
     * 마지막 스냅샷 이후 발생한 가입/탈퇴 이벤트를 집계하고, 현재 총 멤버 수와 함께 저장한다.
     */
    @Transactional
    public void aggregateGuildMemberStats() {
        List<Long> guildIds = guildService.findAllGuildIds();
        if (guildIds.isEmpty()) return;

        // 같은 cron 실행 = 같은 스냅샷 시점. µs 절단으로 PostgreSQL timestamp(6)과 정밀도 일치.
        // TODO: 거의 없을거 같깉 한데 t_jvm과 t_commit 사이 aggregation 실행 시 이벤트 누락 가능. 필요 시 skew window 적용. 당장은 X
        Instant runAt = Instant.now().truncatedTo(ChronoUnit.MICROS);

        Map<Long, Instant> latestStatAt = guildMemberStatService.findLatestCreatedAtPerGuild();

        // 길드를 since 값 기준으로 그룹화. steady state에서는 1개 그룹으로 수렴
        Map<Instant, List<Long>> guildsBySince = new HashMap<>();
        for (Long guildId : guildIds) {
            Instant since = latestStatAt.getOrDefault(guildId, Instant.EPOCH);
            guildsBySince.computeIfAbsent(since, k -> new ArrayList<>()).add(guildId);
        }

        // since별로 단일 GROUP BY 카운트 쿼리. 이벤트 row가 JVM에 들어오지 않음
        Map<Long, Map<GuildMemberEventType, Integer>> eventCounts = new HashMap<>();
        for (Map.Entry<Instant, List<Long>> entry : guildsBySince.entrySet()) {
            for (Object[] row : guildMemberEventService.countByGuildAndTypeAfter(entry.getValue(), entry.getKey(), runAt)) {
                Long guildId = (Long) row[0];
                GuildMemberEventType type = (GuildMemberEventType) row[1];
                int count = ((Number) row[2]).intValue();
                eventCounts
                    .computeIfAbsent(guildId, k -> new HashMap<>())
                    .put(type, count);
            }
        }

        // 길드별 현재 총 멤버 수
        Map<Long, Long> totalMembers = guildMemberService.countPerGuild();

        // 각 길드별 stat을 누적 후 배치 insert
        List<GuildMemberStat> stats = new ArrayList<>(guildIds.size());
        for (Long guildId : guildIds) {
            Map<GuildMemberEventType, Integer> counts = eventCounts.getOrDefault(guildId, Map.of());
            int joined = counts.getOrDefault(GuildMemberEventType.JOIN, 0);
            int left = counts.getOrDefault(GuildMemberEventType.LEAVE, 0);
            int total = totalMembers.getOrDefault(guildId, 0L).intValue();

            stats.add(GuildMemberStat.of(guildId, joined, left, total, runAt));
        }
        guildMemberStatService.createGuildMemberStats(stats);
    }
}
