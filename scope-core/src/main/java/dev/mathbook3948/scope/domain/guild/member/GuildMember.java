package dev.mathbook3948.scope.domain.guild.member;

import dev.mathbook3948.scope.domain.guild.Guild;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuildMember {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guild_member_seq_gen")
    @SequenceGenerator(name = "guild_member_seq_gen", sequenceName = "t_scp_guild_member_seq", allocationSize = 50)
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

    @Column(name = "account_created_at", nullable = false, updatable = false)
    private Instant accountCreatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, updatable = true)
    private Instant updatedAt;

    public void updateName(String name) {
        this.name = name;
    }

    public void updateAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
