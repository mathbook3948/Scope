package dev.mathbook3948.scope.domain.guild.thread;

public record GuildThreadEventInfo(
    Long guildId,
    Long parentChannelId,
    Long threadId,
    Long ownerId,
    String name
) {}
