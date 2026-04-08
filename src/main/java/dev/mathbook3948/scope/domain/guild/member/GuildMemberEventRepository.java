package dev.mathbook3948.scope.domain.guild.member;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GuildMemberEventRepository extends JpaRepository<GuildMemberEvent, Long> {

    int countByGuildIdAndEventTypeAndCreatedAtAfter(Long guildId, GuildMemberEventType eventType, Instant after);
}
