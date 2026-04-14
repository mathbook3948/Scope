package dev.mathbook3948.scope.domain.guild.voice;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "t_scp_guild_voice_stat", indexes = {
        @Index(name = "idx_voice_stat_created_at", columnList = "created_at"),
        @Index(name = "idx_voice_stat_guild_id", columnList = "guild_id")
})
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuildVoiceStat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "voice_stat_seq_gen")
    @SequenceGenerator(name = "voice_stat_seq_gen", sequenceName = "t_scp_guild_voice_stat_seq", allocationSize = 50)
    @Column(name = "voice_stat_seq")
    private Long voiceStatSeq;

    @Column(name = "guild_id", nullable = false)
    private Long guildId;

    @Column(name = "channel_id", nullable = false)
    private Long channelId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "duration", nullable = false)
    private Long duration;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
