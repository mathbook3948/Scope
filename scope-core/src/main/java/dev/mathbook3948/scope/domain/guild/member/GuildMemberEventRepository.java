package dev.mathbook3948.scope.domain.guild.member;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuildMemberEventRepository extends JpaRepository<GuildMemberEvent, Long> {

    int countByGuildIdAndEventTypeAndCreatedAtAfter(Long guildId, GuildMemberEventType eventType, Instant after);

    @Query("""
            SELECT new dev.mathbook3948.scope.domain.guild.member.GuildMemberEventCountView(e.guildId, e.eventType, COUNT(e))
            FROM GuildMemberEvent e
            WHERE e.guildId IN :guildIds
              AND e.createdAt > :since
              AND e.createdAt <= :runAt
            GROUP BY e.guildId, e.eventType
            """)
    List<GuildMemberEventCountView> countByGuildAndTypeAfter(
        @Param("guildIds") List<Long> guildIds,
        @Param("since") Instant since,
        @Param("runAt") Instant runAt
    );

    @Modifying
    @Query("DELETE FROM GuildMemberEvent e WHERE e.guildId = :guildId")
    void deleteAllByGuildId(@Param("guildId") Long guildId);

    @Modifying
    @Query("DELETE FROM GuildMemberEvent e WHERE e.createdAt < :before")
    void deleteAllByCreatedAtBefore(@Param("before") Instant before);
}
