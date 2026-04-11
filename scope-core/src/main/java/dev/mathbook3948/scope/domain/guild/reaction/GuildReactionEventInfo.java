package dev.mathbook3948.scope.domain.guild.reaction;

public record GuildReactionEventInfo(
    Long guildId,
    Long channelId,
    Long messageId,
    Long memberId,
    String emoji
) {}
