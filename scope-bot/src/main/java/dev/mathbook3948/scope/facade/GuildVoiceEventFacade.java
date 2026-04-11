package dev.mathbook3948.scope.facade;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.mathbook3948.scope.domain.guild.voice.GuildVoiceEventInfo;
import dev.mathbook3948.scope.domain.guild.voice.GuildVoiceEventService;
import dev.mathbook3948.scope.domain.guild.voice.GuildVoiceEventType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildVoiceEventFacade {

    private final GuildVoiceEventService guildVoiceEventService;

    @Transactional
    public void onVoiceJoin(GuildVoiceEventInfo info) {
        guildVoiceEventService.createGuildVoiceEvent(info, GuildVoiceEventType.JOIN);
    }

    @Transactional
    public void onVoiceLeave(GuildVoiceEventInfo info) {
        guildVoiceEventService.createGuildVoiceEvent(info, GuildVoiceEventType.LEAVE);
    }

    @Transactional
    public void onVoiceMove(GuildVoiceEventInfo info) {
        guildVoiceEventService.createGuildVoiceEvent(info, GuildVoiceEventType.MOVE);
    }

    @Transactional
    public void cleanupGuildVoiceEvents(Instant before) {
        guildVoiceEventService.deleteAllBefore(before);
    }
}
