package dev.mathbook3948.scope.job;

import java.time.Instant;

import org.jobrunr.scheduling.JobScheduler;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import dev.mathbook3948.scope.facade.GuildReactionEventFacade;
import dev.mathbook3948.scope.properties.JobProperties;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GuildReactionEventCleanupJob {

    private final JobScheduler jobScheduler;

    private final JobProperties jobProperties;

    private final GuildReactionEventFacade guildReactionEventFacade;

    @EventListener(ApplicationReadyEvent.class)
    public void register() {
        jobScheduler.scheduleRecurrently("GuildReactionEventCleanupJob", jobProperties.guildReactionEventCleanup().cron(), this::execute);
    }

    public void execute() {
        Instant cutoff = Instant.now().minus(jobProperties.guildReactionEventCleanup().retention());
        guildReactionEventFacade.cleanupGuildReactionEvents(cutoff);
    }
}
