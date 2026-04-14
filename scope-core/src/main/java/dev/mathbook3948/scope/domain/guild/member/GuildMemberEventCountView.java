package dev.mathbook3948.scope.domain.guild.member;

public record GuildMemberEventCountView(Long guildId, GuildMemberEventType eventType, Long count) {
}
