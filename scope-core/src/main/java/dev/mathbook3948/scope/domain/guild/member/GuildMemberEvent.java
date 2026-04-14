package dev.mathbook3948.scope.domain.guild.member;


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
@Table(name = "t_scp_guild_member_event", indexes = {
    @Index(name = "idx_guild_member_event_created_at", columnList = "created_at"),
    @Index(name = "idx_guild_member_event_guild_id_created_at_event_type",
        columnList = "guild_id, created_at, event_type")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuildMemberEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guild_member_event_seq_gen")
    @SequenceGenerator(name = "guild_member_event_seq_gen", sequenceName = "t_scp_guild_member_event_seq", allocationSize = 50)
    @Column(name = "guild_member_event_seq")
    private Long guildMemberEventSeq;

    @Column(name = "guild_id", nullable = false)
    private Long guildId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private GuildMemberEventType eventType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static GuildMemberEvent of(Long guildId, Long memberId, GuildMemberEventType eventType) {
        GuildMemberEvent event = new GuildMemberEvent();
        event.guildId = guildId;
        event.memberId = memberId;
        event.eventType = eventType;
        return event;
    }
}
