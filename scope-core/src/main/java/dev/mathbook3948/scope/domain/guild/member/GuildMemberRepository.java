package dev.mathbook3948.scope.domain.guild.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GuildMemberRepository extends JpaRepository<GuildMember, Long> {

    Optional<GuildMember> findByGuild_GuildIdAndMemberId(Long guildId, Long memberId);

    List<GuildMember> findByGuild_GuildIdAndMemberIdIn(Long guildId, List<Long> memberIds);

    void deleteByGuild_GuildIdAndMemberId(Long guildId, Long memberId);

    int countByGuild_GuildId(Long guildId);

    @Query("SELECT new dev.mathbook3948.scope.domain.guild.member.GuildMemberCountView(m.guild.guildId, COUNT(m)) FROM GuildMember m GROUP BY m.guild.guildId")
    List<GuildMemberCountView> countPerGuild();

    @Modifying
    @Query("DELETE FROM GuildMember m WHERE m.guild.guildId = :guildId")
    void deleteAllByGuild_GuildId(@Param("guildId") Long guildId);
}
