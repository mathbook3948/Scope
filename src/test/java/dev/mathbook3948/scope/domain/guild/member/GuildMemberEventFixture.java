package dev.mathbook3948.scope.domain.guild.member;

import java.time.Instant;

import org.springframework.test.util.ReflectionTestUtils;

public class GuildMemberEventFixture {
    public static GuildMemberEvent create(Long guildId, Long memberId, GuildMemberEventType type, Instant createdAt) {
        GuildMemberEvent event = GuildMemberEvent.of(guildId, memberId, type);
        ReflectionTestUtils.setField(event, "createdAt", createdAt);
        return event;
    }
}