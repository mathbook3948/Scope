package dev.mathbook3948.scope.domain.guild.voice;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuildVoiceEventRepository extends JpaRepository<GuildVoiceEvent, Long> {

    @Query("""
            SELECT e FROM GuildVoiceEvent e
            WHERE e.guildId = :guildId AND e.memberId = :memberId
            ORDER BY e.createdAt DESC
            LIMIT 1
            """)
    Optional<GuildVoiceEvent> findLatest(
            @Param("guildId") Long guildId,
            @Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM GuildVoiceEvent e WHERE e.guildId = :guildId")
    void deleteAllByGuildId(@Param("guildId") Long guildId);

    @Modifying
    @Query("DELETE FROM GuildVoiceEvent e WHERE e.createdAt < :before")
    void deleteAllByCreatedAtBefore(@Param("before") Instant before);
}
