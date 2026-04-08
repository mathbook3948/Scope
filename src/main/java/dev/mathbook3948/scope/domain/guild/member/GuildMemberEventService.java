package dev.mathbook3948.scope.domain.guild.member;

import java.time.Instant;

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

    @Transactional
    public void createMemberJoinEvent(Long guildId, Long memberId) {
        guildMemberEventRepository.save(GuildMemberEvent.of(guildId, memberId, GuildMemberEventType.JOIN));
    }

    @Transactional
    public void createMemberLeaveEvent(Long guildId, Long memberId) {
        guildMemberEventRepository.save(GuildMemberEvent.of(guildId, memberId, GuildMemberEventType.LEAVE));
    }
}
