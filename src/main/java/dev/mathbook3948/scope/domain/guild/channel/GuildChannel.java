package dev.mathbook3948.scope.domain.guild.channel;

import dev.mathbook3948.scope.domain.guild.Guild;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "t_scp_guild_channel", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"guild_id", "channel_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuildChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guild_channel_seq_gen")
    @SequenceGenerator(name = "guild_channel_seq_gen", sequenceName = "t_scp_guild_channel_seq", allocationSize = 50)
    @Column(name = "guild_channel_seq")
    private Long guildChannelSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id", nullable = false)
    private Guild guild;

    @Column(name = "channel_id", nullable = false)
    private Long channelId;

    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false)
    private GuildChannelType channelType;

    @Column(name = "parent_channel_id")
    private Long parentChannelId;

    @Column(name = "position")
    private Integer position;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, updatable = true)
    private Instant updatedAt;

    public static GuildChannel of(Guild guild, Long channelId, String name, GuildChannelType channelType, Long parentChannelId, Integer position) {
        GuildChannel guildChannel = new GuildChannel();
        guildChannel.guild = guild;
        guildChannel.channelId = channelId;
        guildChannel.name = name;
        guildChannel.channelType = channelType;
        guildChannel.parentChannelId = parentChannelId;
        guildChannel.position = position;
        return guildChannel;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateChannelType(GuildChannelType channelType) {
        this.channelType = channelType;
    }

    public void updateParentChannelId(Long parentChannelId) {
        this.parentChannelId = parentChannelId;
    }

    public void updatePosition(Integer position) {
        this.position = position;
    }
}
