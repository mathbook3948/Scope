package dev.mathbook3948.scope.domain.guild.member;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuildMemberEventRepository extends JpaRepository<GuildMemberEvent, Long> {

    int countByGuildIdAndEventTypeAndCreatedAtAfter(Long guildId, GuildMemberEventType eventType, Instant after);

    @Query("SELECT e FROM GuildMemberEvent e WHERE e.createdAt > :since")
    List<GuildMemberEvent> findAllAfter(@Param("since") Instant since);

    @Modifying
    @Query("DELETE FROM GuildMemberEvent e WHERE e.guildId = :guildId")
    void deleteAllByGuildId(Long guildId);
}
