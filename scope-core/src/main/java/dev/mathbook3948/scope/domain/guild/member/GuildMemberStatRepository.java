package dev.mathbook3948.scope.domain.guild.member;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuildMemberStatRepository extends JpaRepository<GuildMemberStat, Long> {

    Optional<GuildMemberStat> findTopByGuildIdOrderByCreatedAtDesc(Long guildId);

    @Query("SELECT s.guildId, MAX(s.createdAt) FROM GuildMemberStat s GROUP BY s.guildId")
    List<Object[]> findLatestCreatedAtPerGuild();

    @Modifying
    @Query("DELETE FROM GuildMemberStat s WHERE s.guildId = :guildId")
    void deleteAllByGuildId(@Param("guildId") Long guildId);

    @Modifying
    @Query("DELETE FROM GuildMemberStat s WHERE s.createdAt < :before")
    void deleteAllByCreatedAtBefore(@Param("before") Instant before);
}
