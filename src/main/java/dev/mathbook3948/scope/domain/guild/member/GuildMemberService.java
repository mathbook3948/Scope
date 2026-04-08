package dev.mathbook3948.scope.domain.guild.member;

import dev.mathbook3948.scope.domain.guild.GuildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildMemberService {

    private final GuildMemberRepository guildMemberRepository;
    private final GuildRepository guildRepository;

    public GuildMember findByGuildIdAndMemberId(Long guildId, Long memberId) {
        return guildMemberRepository.findByGuild_GuildIdAndMemberId(guildId, memberId)
            .orElseThrow(() -> new IllegalArgumentException("GuildMember not found: guildId=" + guildId + ", memberId=" + memberId));
    }

    @Transactional
    public void upsertGuildMember(Long guildId, Long memberId, String name, String avatarUrl) {
        guildMemberRepository.findByGuild_GuildIdAndMemberId(guildId, memberId)
            .ifPresentOrElse(
                member -> {
                    member.updateName(name);
                    member.updateAvatarUrl(avatarUrl);
                },
                () -> guildMemberRepository.save(GuildMember.of(guildRepository.getReferenceById(guildId), memberId, name, avatarUrl)));
    }

    @Transactional
    public void deleteGuildMember(Long guildId, Long memberId) {
        guildMemberRepository.deleteByGuild_GuildIdAndMemberId(guildId, memberId);
    }

    public int countByGuildId(Long guildId) {
        return guildMemberRepository.countByGuild_GuildId(guildId);
    }

    @Transactional
    public void deleteAllByGuildId(Long guildId) {
        guildMemberRepository.deleteAllByGuild_GuildId(guildId);
    }
}
