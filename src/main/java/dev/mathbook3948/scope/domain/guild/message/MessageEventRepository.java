package dev.mathbook3948.scope.domain.guild.message;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageEventRepository extends JpaRepository<MessageEvent, Long> {

    int countByGuildIdAndEventTypeAndCreatedAtAfter(Long guildId, MessageEventType eventType, Instant after);

    @Modifying
    @Query("DELETE FROM MessageEvent e WHERE e.guildId = :guildId")
    void deleteAllByGuildId(@Param("guildId") Long guildId);
}
