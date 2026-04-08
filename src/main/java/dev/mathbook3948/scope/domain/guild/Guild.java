package dev.mathbook3948.scope.domain.guild;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "t_scp_guild")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guild {

    @Id
    @Column(name = "guild_id")
    private Long guildId;

    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, updatable = true)
    private Instant updatedAt;

    public static Guild of(Long guildId, String name) {
        Guild guild = new Guild();
        guild.guildId = guildId;
        guild.name = name;
        return guild;
    }

    public void updateName(String name) {
        this.name = name;
    }
}
