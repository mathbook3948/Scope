package dev.mathbook3948.scope.domain.guild.member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GuildMemberStatRepository extends JpaRepository<GuildMemberStat, Long> {

    Optional<GuildMemberStat> findTopByGuild_GuildIdOrderByCreatedAtDesc(Long guildId);

    @Query("SELECT s.guild.guildId, MAX(s.createdAt) FROM GuildMemberStat s GROUP BY s.guild.guildId")
    List<Object[]> findLatestCreatedAtPerGuild();

    void deleteAllByGuild_GuildId(Long guildId);
}
