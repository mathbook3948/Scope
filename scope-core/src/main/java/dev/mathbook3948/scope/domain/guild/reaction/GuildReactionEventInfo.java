package dev.mathbook3948.scope.domain.guild.reaction;

import dev.mathbook3948.scope.domain.guild.AuthorType;

public record GuildReactionEventInfo(
    Long guildId,
    Long channelId,
    Long messageId,
    Long memberId,
    String emoji,
    AuthorType authorType
) {}
