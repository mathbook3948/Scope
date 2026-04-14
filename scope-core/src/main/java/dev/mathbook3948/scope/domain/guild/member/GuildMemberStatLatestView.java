package dev.mathbook3948.scope.domain.guild.member;

import java.time.Instant;

public record GuildMemberStatLatestView(Long guildId, Instant latestCreatedAt) {
}
