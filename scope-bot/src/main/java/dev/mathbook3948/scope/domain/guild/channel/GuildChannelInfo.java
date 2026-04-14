package dev.mathbook3948.scope.domain.guild.channel;

public record GuildChannelInfo(
    Long channelId,
    String name,
    GuildChannelType channelType,
    Long parentChannelId,
    Integer position
) {}
