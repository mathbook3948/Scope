package dev.mathbook3948.scope.domain.guild.voice;

import java.time.Instant;

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
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그성 테이블. FK 사용 안함
 */
@Entity
@Table(name = "t_scp_guild_voice_event", indexes = {
        @Index(name = "idx_voice_event_created_at", columnList = "created_at"),
        @Index(name = "idx_voice_event_guild_id_member_id_created_at",
                columnList = "guild_id, member_id, created_at DESC")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuildVoiceEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "voice_event_seq_gen")
    @SequenceGenerator(name = "voice_event_seq_gen", sequenceName = "t_scp_guild_voice_event_seq", allocationSize = 50)
    @Column(name = "voice_event_seq")
    private Long voiceEventSeq;

    @Column(name = "guild_id", nullable = false)
    private Long guildId;

    @Column(name = "channel_id", nullable = false)
    private Long channelId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private GuildVoiceEventType eventType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static GuildVoiceEvent of(GuildVoiceEventInfo info, GuildVoiceEventType eventType) {
        GuildVoiceEvent event = new GuildVoiceEvent();
        event.guildId = info.guildId();
        event.channelId = info.channelId();
        event.memberId = info.memberId();
        event.eventType = eventType;
        event.createdAt = info.createdAt();
        return event;
    }
}
