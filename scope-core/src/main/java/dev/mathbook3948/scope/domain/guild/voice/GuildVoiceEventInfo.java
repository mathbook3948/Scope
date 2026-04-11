package dev.mathbook3948.scope.domain.guild.voice;

public record GuildVoiceEventInfo(
        Long guildId,
        Long channelId,
        Long memberId
) {}
