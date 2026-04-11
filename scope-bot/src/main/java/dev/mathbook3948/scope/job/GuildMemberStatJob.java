package dev.mathbook3948.scope.job;

import org.jobrunr.scheduling.JobScheduler;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import dev.mathbook3948.scope.facade.GuildMemberFacade;
import dev.mathbook3948.scope.properties.JobProperties;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GuildMemberStatJob {

    private final JobScheduler jobScheduler;

    private final JobProperties jobProperties;

    private final GuildMemberFacade guildMemberFacade;

    @EventListener(ApplicationReadyEvent.class)
    public void register() {
        jobScheduler.scheduleRecurrently("GuildMemberStatJob", jobProperties.guildMemberStat(), this::execute);
    }

    public void execute() {
        guildMemberFacade.aggregateGuildMemberStats();
    }
}
