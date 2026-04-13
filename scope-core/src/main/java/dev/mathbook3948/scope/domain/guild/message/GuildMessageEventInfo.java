package dev.mathbook3948.scope.domain.guild.message;

import dev.mathbook3948.scope.domain.guild.AuthorType;

public record GuildMessageEventInfo(
    Long guildId,
    Long channelId,
    Long memberId,
    Long messageId,
    Integer contentLength,
    GuildMessageSourceType sourceType,
    AuthorType authorType
) {}
