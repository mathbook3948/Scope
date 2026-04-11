package dev.mathbook3948.scope.domain.guild.message;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuildMessageEventRepository extends JpaRepository<GuildMessageEvent, Long> {

    int countByGuildIdAndEventTypeAndCreatedAtAfter(Long guildId, GuildMessageEventType eventType, Instant after);

    @Modifying
    @Query("DELETE FROM GuildMessageEvent e WHERE e.guildId = :guildId")
    void deleteAllByGuildId(@Param("guildId") Long guildId);

    @Modifying
    @Query("DELETE FROM GuildMessageEvent e WHERE e.createdAt < :before")
    void deleteAllByCreatedAtBefore(@Param("before") Instant before);
}
