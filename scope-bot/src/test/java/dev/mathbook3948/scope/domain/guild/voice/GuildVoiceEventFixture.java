package dev.mathbook3948.scope.domain.guild.voice;

import java.time.Instant;

import org.springframework.test.util.ReflectionTestUtils;

public class GuildVoiceEventFixture {
    public static GuildVoiceEvent create(GuildVoiceEventInfo info, GuildVoiceEventType type, Instant createdAt) {
        GuildVoiceEvent event = GuildVoiceEvent.builder()
            .guildId(info.guildId())
            .channelId(info.channelId())
            .memberId(info.memberId())
            .eventType(type)
            .createdAt(info.createdAt())
            .build();
        ReflectionTestUtils.setField(event, "createdAt", createdAt);
        return event;
    }
}
