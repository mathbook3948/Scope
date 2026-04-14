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

    @Transactional
    public void upsertGuildMember(Long guildId, GuildMemberInfo info) {
        guildMemberRepository.findByGuild_GuildIdAndMemberId(guildId, info.memberId())
            .map(member -> {
                member.updateName(info.name());
                member.updateAvatarUrl(info.avatarUrl());
                return member;
            })
            .orElseGet(() -> guildMemberRepository.save(
                GuildMember.builder()
                    .guild(guildRepository.getReferenceById(guildId))
                    .memberId(info.memberId())
                    .name(info.name())
                    .avatarUrl(info.avatarUrl())
                    .accountCreatedAt(info.accountCreatedAt())
                    .build()));
    }

    @Transactional
    public void deleteGuildMember(Long guildId, Long memberId) {
        guildMemberRepository.deleteByGuild_GuildIdAndMemberId(guildId, memberId);
    }

    public Map<Long, Long> countPerGuild() {
        Map<Long, Long> result = new HashMap<>();
        for (GuildMemberCountView row : guildMemberRepository.countPerGuild()) {
            result.put(row.guildId(), row.count());
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
                .map(info -> GuildMember.builder()
                    .guild(guildRef)
                    .memberId(info.memberId())
                    .name(info.name())
                    .avatarUrl(info.avatarUrl())
                    .accountCreatedAt(info.accountCreatedAt())
                    .build())
                .toList();
            guildMemberRepository.saveAll(newMembers);
        }
    }

    @Transactional
    public void deleteAllByGuildId(Long guildId) {
        guildMemberRepository.deleteAllByGuild_GuildId(guildId);
    }
}
