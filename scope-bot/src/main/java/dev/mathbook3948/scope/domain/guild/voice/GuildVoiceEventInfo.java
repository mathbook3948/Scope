package dev.mathbook3948.scope.domain.guild.voice;

import java.time.Instant;

public record GuildVoiceEventInfo(
        Long guildId,
        Long channelId,
        Long memberId,
        Instant createdAt
) {}
