package dev.mathbook3948.scope.facade;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.mathbook3948.scope.domain.guild.voice.GuildVoiceEventInfo;
import dev.mathbook3948.scope.domain.guild.voice.GuildVoiceEventService;
import dev.mathbook3948.scope.domain.guild.voice.GuildVoiceEventType;
import dev.mathbook3948.scope.domain.guild.voice.GuildVoiceStatService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildVoiceEventFacade {

    private final GuildVoiceEventService guildVoiceEventService;
    private final GuildVoiceStatService guildVoiceStatService;

    @Transactional
    public void onVoiceJoin(GuildVoiceEventInfo info) {
        guildVoiceEventService.createGuildVoiceEvent(info, GuildVoiceEventType.JOIN);
    }

    @Transactional
    public void onVoiceLeave(GuildVoiceEventInfo info) {
        saveVoiceStat(info);
        guildVoiceEventService.createGuildVoiceEvent(info, GuildVoiceEventType.LEAVE);
    }

    @Transactional
    public void onVoiceMove(GuildVoiceEventInfo joinedInfo, GuildVoiceEventInfo leftInfo) {
        saveVoiceStat(leftInfo);
        guildVoiceEventService.createGuildVoiceEvent(joinedInfo, GuildVoiceEventType.MOVE);
    }

    @Transactional
    public void cleanupGuildVoiceEvents(Instant before) {
        guildVoiceEventService.deleteAllBefore(before);
    }

    @Transactional
    public void cleanupGuildVoiceStats(Instant before) {
        guildVoiceStatService.deleteAllBefore(before);
    }

    private void saveVoiceStat(GuildVoiceEventInfo info) {
        guildVoiceEventService.findLatest(info.guildId(), info.memberId())
                .ifPresent(startEvent -> {
                    if (startEvent.getEventType() == GuildVoiceEventType.LEAVE) {
                        return;
                    }
                    if (!startEvent.getChannelId().equals(info.channelId())) {
                        return;
                    }
                    long duration = Duration.between(startEvent.getCreatedAt(), Instant.now()).getSeconds();
                    guildVoiceStatService.createGuildVoiceStat(info, duration);
                });
    }
}
