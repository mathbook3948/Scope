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

    /**
     * 보관 기간이 지난 GuildVoiceEvent를 일괄 삭제한다.
     *
     * @see dev.mathbook3948.scope.job.GuildVoiceEventCleanupJob
     */
    @Transactional
    public void cleanupGuildVoiceEvents(Instant before) {
        guildVoiceEventService.deleteAllBefore(before);
    }

    /**
     * 보관 기간이 지난 GuildVoiceStat을 일괄 삭제한다.
     *
     * @see dev.mathbook3948.scope.job.GuildVoiceStatCleanupJob
     */
    @Transactional
    public void cleanupGuildVoiceStats(Instant before) {
        guildVoiceStatService.deleteAllBefore(before);
    }

    private void saveVoiceStat(GuildVoiceEventInfo info) {
        //TODO 캐시처리
        guildVoiceEventService.findLatest(info.guildId(), info.memberId())
            .ifPresent(startEvent -> {
                if (startEvent.getEventType() == GuildVoiceEventType.LEAVE) {
                    return;
                }
                if (!startEvent.getChannelId().equals(info.channelId())) {
                    return;
                }
                long duration = Duration.between(startEvent.getCreatedAt(), info.createdAt()).getSeconds();
                guildVoiceStatService.createGuildVoiceStat(info, duration);
            });
    }
}
