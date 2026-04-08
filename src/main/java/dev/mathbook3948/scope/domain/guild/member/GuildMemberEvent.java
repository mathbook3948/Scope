package dev.mathbook3948.scope.domain.guild.member;


import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import dev.mathbook3948.scope.domain.guild.Guild;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_scp_guild_member_event", indexes = {
    @Index(name = "idx_guild_member_event_created_at", columnList = "created_at"),
    @Index(name = "idx_guild_member_event_guild_id", columnList = "guild_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuildMemberEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guild_member_event_seq")
    private Long guildMemberEventSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id", nullable = false)
    private Guild guild;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "guild_id", referencedColumnName = "guild_id", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, insertable = false, updatable = false)
    })
    private GuildMember member;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private GuildMemberEventType eventType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static GuildMemberEvent of(Guild guild, GuildMember member, GuildMemberEventType eventType) {
        GuildMemberEvent event = new GuildMemberEvent();
        event.guild = guild;
        event.member = member;
        event.memberId = member.getMemberId();
        event.eventType = eventType;
        return event;
    }
}
