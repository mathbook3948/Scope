package dev.mathbook3948.scope.domain.guild.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuildMemberRepository extends JpaRepository<GuildMember, Long> {

    Optional<GuildMember> findByGuild_GuildIdAndMemberId(Long guildId, Long memberId);

    void deleteByGuild_GuildIdAndMemberId(Long guildId, Long memberId);

    int countByGuild_GuildId(Long guildId);

    void deleteAllByGuild_GuildId(Long guildId);
}
