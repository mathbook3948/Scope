package dev.mathbook3948.scope.domain.guild;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GuildRepository extends JpaRepository<Guild, Long> {

    @Query("SELECT g.guildId FROM Guild g")
    List<Long> findAllGuildIds();
}
