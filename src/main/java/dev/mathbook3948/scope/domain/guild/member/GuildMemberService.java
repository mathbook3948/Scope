package dev.mathbook3948.scope.domain.guild.member;

import dev.mathbook3948.scope.domain.guild.Guild;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildMemberService {

    private final GuildMemberRepository guildMemberRepository;

    @Transactional
    public void createGuildMember(Long guildId, Long memberId, String name, String avatarUrl) {
        guildMemberRepository.findByGuild_GuildIdAndMemberId(guildId, memberId)
            .ifPresentOrElse(
                member -> member.update(name, avatarUrl),
                () -> guildMemberRepository.save(GuildMember.of(Guild.of(guildId, null), memberId, name, avatarUrl)));
    }

    @Transactional
    public void deleteGuildMember(Long guildId, Long memberId) {
        guildMemberRepository.deleteByGuild_GuildIdAndMemberId(guildId, memberId);
    }

    @Transactional
    public void updateGuildMember(Long guildId, Long memberId, String name, String avatarUrl) {
        
    }
}
