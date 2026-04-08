package dev.mathbook3948.scope.domain.guild.member;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildMemberStatService {

    private final GuildMemberStatRepository guildMemberStatRepository;

    public Optional<GuildMemberStat> findLatestByGuildId(Long guildId) {
        return guildMemberStatRepository.findTopByGuildIdOrderByCreatedAtDesc(guildId);
    }

    @Transactional
    public void createGuildMemberStat(Long guildId, int joinedMembers, int leftMembers, int totalMembers) {
        guildMemberStatRepository.save(GuildMemberStat.of(guildId, joinedMembers, leftMembers, totalMembers));
    }
}
