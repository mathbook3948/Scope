package dev.mathbook3948.scope.facade;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.mathbook3948.scope.domain.guild.thread.GuildThreadEventInfo;
import dev.mathbook3948.scope.domain.guild.thread.GuildThreadEventService;
import dev.mathbook3948.scope.domain.guild.thread.GuildThreadEventType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildThreadEventFacade {

    private final GuildThreadEventService guildThreadEventService;

    @Transactional
    public void onThreadCreate(GuildThreadEventInfo info) {
        guildThreadEventService.createGuildThreadEvent(info, GuildThreadEventType.CREATE);
    }

    @Transactional
    public void onThreadDelete(GuildThreadEventInfo info) {
        guildThreadEventService.createGuildThreadEvent(info, GuildThreadEventType.DELETE);
    }

    /**
     * 보관 기간이 지난 GuildThreadEvent를 일괄 삭제한다.
     *
     * @see dev.mathbook3948.scope.job.GuildThreadEventCleanupJob
     */
    @Transactional
    public void cleanupGuildThreadEvents(Instant before) {
        guildThreadEventService.deleteAllBefore(before);
    }
}
