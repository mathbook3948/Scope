package dev.mathbook3948.scope.domain.guild.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GuildMemberStatRepository extends JpaRepository<GuildMemberStat, Long> {

    Optional<GuildMemberStat> findTopByGuild_GuildIdOrderByCreatedAtDesc(Long guildId);
}
