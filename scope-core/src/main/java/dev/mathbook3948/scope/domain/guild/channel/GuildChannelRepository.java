package dev.mathbook3948.scope.domain.guild.channel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GuildChannelRepository extends JpaRepository<GuildChannel, Long> {

    Optional<GuildChannel> findByGuild_GuildIdAndChannelId(Long guildId, Long channelId);

    List<GuildChannel> findByGuild_GuildIdAndChannelIdIn(Long guildId, List<Long> channelIds);

    void deleteByGuild_GuildIdAndChannelId(Long guildId, Long channelId);

    @Modifying
    @Query("DELETE FROM GuildChannel c WHERE c.guild.guildId = :guildId")
    void deleteAllByGuild_GuildId(@Param("guildId") Long guildId);
}
