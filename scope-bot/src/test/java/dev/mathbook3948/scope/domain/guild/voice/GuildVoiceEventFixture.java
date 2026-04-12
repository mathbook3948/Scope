package dev.mathbook3948.scope.domain.guild.voice;

import java.time.Instant;

import org.springframework.test.util.ReflectionTestUtils;

public class GuildVoiceEventFixture {
    public static GuildVoiceEvent create(GuildVoiceEventInfo info, GuildVoiceEventType type, Instant createdAt) {
        GuildVoiceEvent event = GuildVoiceEvent.of(info, type);
        ReflectionTestUtils.setField(event, "createdAt", createdAt);
        return event;
    }
}
