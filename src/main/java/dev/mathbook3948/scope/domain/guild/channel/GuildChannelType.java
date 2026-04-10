package dev.mathbook3948.scope.domain.guild.channel;

public enum GuildChannelType {
    TEXT, VOICE, CATEGORY, NEWS, STAGE, FORUM, MEDIA, UNKNOWN;

    public static GuildChannelType from(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
