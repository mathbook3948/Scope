package dev.mathbook3948.scope.domain.guild.thread;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuildThreadEventRepository extends JpaRepository<GuildThreadEvent, Long> {

    @Modifying
    @Query("DELETE FROM GuildThreadEvent e WHERE e.guildId = :guildId")
    void deleteAllByGuildId(@Param("guildId") Long guildId);

    @Modifying
    @Query("DELETE FROM GuildThreadEvent e WHERE e.createdAt < :before")
    void deleteAllByCreatedAtBefore(@Param("before") Instant before);
}
