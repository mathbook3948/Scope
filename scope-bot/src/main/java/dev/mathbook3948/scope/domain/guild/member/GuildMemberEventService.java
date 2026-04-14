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

    public List<GuildMemberEventCountView> countByGuildAndTypeAfter(List<Long> guildIds, Instant since, Instant runAt) {
        return guildMemberEventRepository.countByGuildAndTypeAfter(guildIds, since, runAt);
    }

    @Transactional
    public void deleteAllByGuildId(Long guildId) {
        guildMemberEventRepository.deleteAllByGuildId(guildId);
    }

    @Transactional
    public void deleteAllBefore(Instant before) {
        guildMemberEventRepository.deleteAllByCreatedAtBefore(before);
    }

    @Transactional
    public void createMemberEvent(Long guildId, Long memberId, GuildMemberEventType eventType) {
        guildMemberEventRepository.save(GuildMemberEvent.builder()
            .guildId(guildId)
            .memberId(memberId)
            .eventType(eventType)
            .build());
    }
}
