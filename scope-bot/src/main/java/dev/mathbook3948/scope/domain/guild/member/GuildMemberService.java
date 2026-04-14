package dev.mathbook3948.scope.domain.guild.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import dev.mathbook3948.scope.domain.guild.Guild;
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
    public GuildMember upsertGuildMember(Long guildId, GuildMemberInfo info) {
        return guildMemberRepository.findByGuild_GuildIdAndMemberId(guildId, info.memberId())
            .map(member -> {
                member.updateName(info.name());
                member.updateAvatarUrl(info.avatarUrl());
                return member;
            })
            .orElseGet(() -> guildMemberRepository.save(
                GuildMember.of(
                    guildRepository.getReferenceById(guildId),
                    info.memberId(),
                    info.name(),
                    info.avatarUrl(),
                    info.accountCreatedAt()
                )));
    }

    @Transactional
    public void deleteGuildMember(Long guildId, Long memberId) {
        guildMemberRepository.deleteByGuild_GuildIdAndMemberId(guildId, memberId);
    }

    public int countByGuildId(Long guildId) {
        return guildMemberRepository.countByGuild_GuildId(guildId);
    }

    public Map<Long, Long> countPerGuild() {
        Map<Long, Long> result = new HashMap<>();
        for (Object[] row : guildMemberRepository.countPerGuild()) {
            result.put((Long) row[0], (Long) row[1]);
        }
        return result;
    }

    @Transactional
    public void upsertGuildMembers(Long guildId, List<GuildMemberInfo> members) {
        List<Long> memberIds = members.stream().map(GuildMemberInfo::memberId).toList();

        Map<Long, GuildMember> existingMap = guildMemberRepository
            .findByGuild_GuildIdAndMemberIdIn(guildId, memberIds)
            .stream()
            .collect(Collectors.toMap(GuildMember::getMemberId, Function.identity()));

        List<GuildMemberInfo> newMemberInfos = new ArrayList<>();

        for (GuildMemberInfo info : members) {
            GuildMember existing = existingMap.get(info.memberId());
            if (existing != null) {
                if (!existing.getName().equals(info.name())) {
                    existing.updateName(info.name());
                }
                if (!existing.getAvatarUrl().equals(info.avatarUrl())) {
                    existing.updateAvatarUrl(info.avatarUrl());
                }
            } else {
                newMemberInfos.add(info);
            }
        }

        if (!newMemberInfos.isEmpty()) {
            Guild guildRef = guildRepository.getReferenceById(guildId);
            List<GuildMember> newMembers = newMemberInfos.stream()
                .map(info -> GuildMember.of(guildRef, info.memberId(), info.name(), info.avatarUrl(), info.accountCreatedAt()))
                .toList();
            guildMemberRepository.saveAll(newMembers);
        }
    }

    @Transactional
    public void deleteAllByGuildId(Long guildId) {
        guildMemberRepository.deleteAllByGuild_GuildId(guildId);
    }
}
