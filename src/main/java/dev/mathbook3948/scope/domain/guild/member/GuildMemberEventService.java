package dev.mathbook3948.scope.domain.guild.member;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.mathbook3948.scope.domain.guild.Guild;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildMemberEventService {

    private final GuildMemberEventRepository guildMemberEventRepository;

    public int countByGuildIdAndEventTypeAfter(Long guildId, GuildMemberEventType eventType, Instant after) {
        return guildMemberEventRepository.countByGuild_GuildIdAndEventTypeAndCreatedAtAfter(guildId, eventType, after);
    }

    @Transactional
    public void deleteAllByGuildId(Long guildId) {
        guildMemberEventRepository.deleteAllByGuild_GuildId(guildId);
    }

    @Transactional
    public void createMemberEvent(Guild guild, GuildMember member, GuildMemberEventType eventType) {
        guildMemberEventRepository.save(GuildMemberEvent.of(guild, member, eventType));
    }
}
