package dev.mathbook3948.scope.domain.guild.message;

public record GuildMessageEventInfo(
    Long guildId,
    Long channelId,
    Long memberId,
    Long messageId,
    Integer contentLength
) {}
