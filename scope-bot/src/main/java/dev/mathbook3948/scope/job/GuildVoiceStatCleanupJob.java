package dev.mathbook3948.scope.job;

import java.time.Instant;

import org.jobrunr.scheduling.JobScheduler;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import dev.mathbook3948.scope.facade.GuildVoiceEventFacade;
import dev.mathbook3948.scope.properties.JobProperties;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GuildVoiceStatCleanupJob {

    private final JobScheduler jobScheduler;

    private final JobProperties jobProperties;

    private final GuildVoiceEventFacade guildVoiceEventFacade;

    @EventListener(ApplicationReadyEvent.class)
    public void register() {
        jobScheduler.scheduleRecurrently("GuildVoiceStatCleanupJob", jobProperties.guildVoiceStatCleanup().cron(), this::execute);
    }

    public void execute() {
        Instant cutoff = Instant.now().minus(jobProperties.guildVoiceStatCleanup().retention());
        guildVoiceEventFacade.cleanupGuildVoiceStats(cutoff);
    }
}
