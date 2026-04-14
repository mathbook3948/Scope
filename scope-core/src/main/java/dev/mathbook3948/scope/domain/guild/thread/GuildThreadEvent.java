package dev.mathbook3948.scope.domain.guild.thread;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그성 테이블. FK 사용 안함
 */
@Entity
@Table(name = "t_scp_guild_thread_event", indexes = {
        @Index(name = "idx_thread_event_created_at", columnList = "created_at"),
        @Index(name = "idx_thread_event_guild_id", columnList = "guild_id")
})
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuildThreadEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "thread_event_seq_gen")
    @SequenceGenerator(name = "thread_event_seq_gen", sequenceName = "t_scp_guild_thread_event_seq", allocationSize = 50)
    @Column(name = "thread_event_seq")
    private Long threadEventSeq;

    @Column(name = "guild_id", nullable = false)
    private Long guildId;

    @Column(name = "parent_channel_id", nullable = false)
    private Long parentChannelId;

    @Column(name = "thread_id", nullable = false)
    private Long threadId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private GuildThreadEventType eventType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
