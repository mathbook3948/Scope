package dev.mathbook3948.scope.domain.guild.message;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import dev.mathbook3948.scope.domain.guild.AuthorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private GuildMessageSourceType sourceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "author_type", nullable = false)
    private AuthorType authorType;

    @Column(name = "reply_to_message_id")
    private Long replyToMessageId;

    @Column(name = "content_length")
    private Integer contentLength;

    @Column(name = "mention_count")
    private Integer mentionCount;

    @Column(name = "attachment_count")
    private Integer attachmentCount;

    @Column(name = "has_link")
    private Boolean hasLink;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
