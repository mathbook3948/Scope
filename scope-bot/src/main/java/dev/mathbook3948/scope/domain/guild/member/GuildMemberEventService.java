package dev.mathbook3948.scope.domain.guild.member;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildMemberEventService {

    private final GuildMemberEventRepository guildMemberEventRepository;

    public int countByGuildIdAndEventTypeAfter(Long guildId, GuildMemberEventType eventType, Instant after) {
        return guildMemberEventRepository.countByGuildIdAndEventTypeAndCreatedAtAfter(guildId, eventType, after);
    }

    public List<GuildMemberEvent> findAllAfter(Instant since) {
        return guildMemberEventRepository.findAllAfter(since);
    }

    @Transactional
    public void deleteAllByGuildId(Long guildId) {
        guildMemberEventRepository.deleteAllByGuildId(guildId);
    }

    @Transactional
    public void createMemberEvent(Long guildId, Long memberId, GuildMemberEventType eventType) {
        guildMemberEventRepository.save(GuildMemberEvent.of(guildId, memberId, eventType));
    }
}
