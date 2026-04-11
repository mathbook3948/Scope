package dev.mathbook3948.scope.domain.guild.reaction;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildReactionEventService {

    private final GuildReactionEventRepository guildReactionEventRepository;

    @Transactional
    public void createGuildReactionEvent(GuildReactionEventInfo reaction, GuildReactionEventType eventType) {
        guildReactionEventRepository.save(GuildReactionEvent.of(reaction, eventType));
    }

    @Transactional
    public void deleteAllByGuildId(Long guildId) {
        guildReactionEventRepository.deleteAllByGuildId(guildId);
    }

    @Transactional
    public void deleteAllBefore(Instant before) {
        guildReactionEventRepository.deleteAllByCreatedAtBefore(before);
    }
}
