package dev.mathbook3948.scope.domain.guild.reaction;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuildReactionEventRepository extends JpaRepository<GuildReactionEvent, Long> {

    @Modifying
    @Query("DELETE FROM GuildReactionEvent e WHERE e.guildId = :guildId")
    void deleteAllByGuildId(@Param("guildId") Long guildId);

    @Modifying
    @Query("DELETE FROM GuildReactionEvent e WHERE e.createdAt < :before")
    void deleteAllByCreatedAtBefore(@Param("before") Instant before);
}
