package dev.mathbook3948.scope.domain.guild.member;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildMemberEventService {

    private final GuildMemberEventRepository guildMemberEventRepository;

    @Transactional
    public void createMemberJoinEvent(Long guildId, Long memberId) {
        guildMemberEventRepository.save(GuildMemberEvent.of(guildId, memberId, GuildMemberEventType.JOIN));
    }

    @Transactional
    public void createMemberLeaveEvent(Long guildId, Long memberId) {
        guildMemberEventRepository.save(GuildMemberEvent.of(guildId, memberId, GuildMemberEventType.LEAVE));
    }
}
