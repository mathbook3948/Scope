package dev.mathbook3948.scope.domain.guild.message;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그성 테이블. FK 사용 안함
 */
@Entity
@Table(name = "t_scp_guild_message_event", indexes = {
    @Index(name = "idx_message_event_created_at", columnList = "created_at"),
    @Index(name = "idx_message_event_channel_id", columnList = "channel_id"),
    @Index(name = "idx_message_event_guild_id_member_id", columnList = "guild_id, member_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuildMessageEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_event_seq_gen")
    @SequenceGenerator(name = "message_event_seq_gen", sequenceName = "t_scp_guild_message_event_seq", allocationSize = 50)
    @Column(name = "message_event_seq")
    private Long messageEventSeq;

    @Column(name = "guild_id", nullable = false)
    private Long guildId;

    @Column(name = "channel_id", nullable = false)
    private Long channelId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private GuildMessageEventType eventType;

    @Column(name = "content_length")
    private Integer contentLength;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static GuildMessageEvent of(Long guildId, Long channelId, Long memberId, Long messageId,
                                   GuildMessageEventType eventType, Integer contentLength) {
        GuildMessageEvent event = new GuildMessageEvent();
        event.guildId = guildId;
        event.channelId = channelId;
        event.memberId = memberId;
        event.messageId = messageId;
        event.eventType = eventType;
        event.contentLength = contentLength;
        return event;
    }

    public static GuildMessageEvent ofDelete(Long guildId, Long channelId, Long messageId) {
        GuildMessageEvent event = new GuildMessageEvent();
        event.guildId = guildId;
        event.channelId = channelId;
        event.messageId = messageId;
        event.eventType = GuildMessageEventType.DELETE;
        return event;
    }
}
