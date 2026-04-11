package dev.mathbook3948.scope.domain.guild;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "t_scp_guild")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guild implements Persistable<Long>{

    @Transient
    private boolean isNew = true;

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

    @Override
    public Long getId() { return this.guildId; }

    @Override
    public boolean isNew() { return isNew; }

    @PostPersist
    @PostLoad
    void markNotNew() { this.isNew = false; }

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
