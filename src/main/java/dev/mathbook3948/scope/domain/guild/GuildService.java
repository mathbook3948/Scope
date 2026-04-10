package dev.mathbook3948.scope.domain.guild;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildService {

    private final GuildRepository guildRepository;

    public List<Guild> findAll() {
        return guildRepository.findAll();
    }

    public Guild getReferenceById(Long guildId) {
        return guildRepository.getReferenceById(guildId);
    }

    @Transactional
    public void createGuild(Long guildId, String name) {
        guildRepository.save(Guild.of(guildId, name));
    }

    @Transactional
    public void upsertGuild(Long guildId, String name) {
        guildRepository.findById(guildId)
            .ifPresentOrElse(
                guild -> guild.updateName(name),
                () -> guildRepository.save(Guild.of(guildId, name))
            );
    }

    @Transactional
    public void deleteGuild(Long guildId) {
        guildRepository.deleteById(guildId);
    }

    /**
     * Guild 명을 업데이트한다
     * @param guildId Guild ID
     * @param name Guild 명
     * 
     * @throws IllegalStateException Guild ID에 해당하는 Guild가 존재하지 않을경우
     */
    @Transactional
    public void updateGuild(Long guildId, String name) {
        Guild guild = guildRepository.findById(guildId).orElseThrow(() -> new IllegalStateException("Guild not found: " + guildId));
        guild.updateName(name);
    }
}
