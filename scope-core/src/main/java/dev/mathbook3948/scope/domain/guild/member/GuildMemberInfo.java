package dev.mathbook3948.scope.domain.guild.member;

import java.time.Instant;

public record GuildMemberInfo(
    Long memberId,
    String name,
    String avatarUrl,
    Instant accountCreatedAt
) {}
