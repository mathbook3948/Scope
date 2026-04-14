package dev.mathbook3948.scope.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.mathbook3948.scope.domain.guild.GuildService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberEventService;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberEventType;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberInfo;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberService;
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

        // 같은 cron 실행 = 같은 스냅샷 시점. 다음 회차 since-그룹화를 위해 모든 stat 동일 시각.
        // µs로 절단해 PostgreSQL timestamp(6) 정밀도와 일치시켜 저장-읽기 라운드트립 손실 제거.
        // TODO: READ COMMITTED race window — JVM에서 @CreationTimestamp 찍힌 시점(t_jvm)과
        //  해당 Tx commit 시점(t_commit) 사이에 aggregation 쿼리가 실행되면 이벤트가 영구 누락된다.
        //  (aggregation이 t_jvm < t_read < t_commit 사이 읽으면 못 보고, 다음 회차는 since > t_jvm이라 제외)
        //  완화 방안: runAt에 skew window 적용(`now().minus(N)`). 현재는 봇 쓰기 commit 지연이 ms 단위라 무시.
        //  관측 시 대응 우선순위 재평가 필요.
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

        // 각 길드별로 stat insert
        for (Long guildId : guildIds) {
            Map<GuildMemberEventType, Integer> counts = eventCounts.getOrDefault(guildId, Map.of());
            int joined = counts.getOrDefault(GuildMemberEventType.JOIN, 0);
            int left = counts.getOrDefault(GuildMemberEventType.LEAVE, 0);
            int total = totalMembers.getOrDefault(guildId, 0L).intValue();

            guildMemberStatService.createGuildMemberStat(guildId, joined, left, total, runAt);
        }
    }
}
