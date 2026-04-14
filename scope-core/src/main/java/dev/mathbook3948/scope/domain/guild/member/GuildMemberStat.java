package dev.mathbook3948.scope.domain.guild.member;


import java.time.Instant;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그성 테이블. FK 사용 안함
 */
@Entity
@Table(name = "t_scp_guild_member_stat", indexes = {
    @Index(name = "idx_guild_member_stat_created_at", columnList = "created_at"),
    @Index(name = "idx_guild_member_stat_guild_id", columnList = "guild_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuildMemberStat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guild_member_stat_seq_gen")
    @SequenceGenerator(name = "guild_member_stat_seq_gen", sequenceName = "t_scp_guild_member_stat_seq", allocationSize = 50)
    @Column(name = "guild_member_stat_seq")
    private Long guildMemberStatSeq;

    @Column(name = "guild_id", nullable = false)
    private Long guildId;

    @Column(name = "joined_members", nullable = false)
    private Integer joinedMembers;

    @Column(name = "left_members", nullable = false)
    private Integer leftMembers;

    @Column(name = "total_members", nullable = false)
    private Integer totalMembers;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static GuildMemberStat of(Long guildId, Integer joinedMembers, Integer leftMembers, Integer totalMembers, Instant createdAt) {
        GuildMemberStat stat = new GuildMemberStat();
        stat.guildId = guildId;
        stat.joinedMembers = joinedMembers;
        stat.leftMembers = leftMembers;
        stat.totalMembers = totalMembers;
        stat.createdAt = createdAt;
        return stat;
    }
}
