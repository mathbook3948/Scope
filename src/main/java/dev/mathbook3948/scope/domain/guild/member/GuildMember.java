package dev.mathbook3948.scope.domain.guild.member;

import dev.mathbook3948.scope.domain.guild.Guild;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "t_scp_guild_member", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"guild_id", "member_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuildMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guild_member_seq")
    private Long guildMemberSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id", nullable = false)
    private Guild guild;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @Column(name = "avatar_url", nullable = false, columnDefinition = "text")
    private String avatarUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, updatable = true)
    private Instant updatedAt;

    public static GuildMember of(Guild guild, Long memberId, String name, String avatarUrl) {
        GuildMember guildMember = new GuildMember();
        guildMember.guild = guild;
        guildMember.memberId = memberId;
        guildMember.name = name;
        guildMember.avatarUrl = avatarUrl;
        return guildMember;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
