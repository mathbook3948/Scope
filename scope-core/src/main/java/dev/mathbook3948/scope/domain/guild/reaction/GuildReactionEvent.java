package dev.mathbook3948.scope.domain.guild.reaction;

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
@Table(name = "t_scp_guild_reaction_event", indexes = {
    @Index(name = "idx_reaction_event_created_at", columnList = "created_at"),
    @Index(name = "idx_reaction_event_guild_id", columnList = "guild_id")
})
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuildReactionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reaction_event_seq_gen")
    @SequenceGenerator(name = "reaction_event_seq_gen", sequenceName = "t_scp_guild_reaction_event_seq", allocationSize = 50)
    @Column(name = "reaction_event_seq")
    private Long reactionEventSeq;

    @Column(name = "guild_id", nullable = false)
    private Long guildId;

    @Column(name = "channel_id", nullable = false)
    private Long channelId;

    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "emoji", nullable = false)
    private String emoji;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private GuildReactionEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "author_type", nullable = false)
    private AuthorType authorType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
