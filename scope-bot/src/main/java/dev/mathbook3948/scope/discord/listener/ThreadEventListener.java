package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.discord.utils.JdaMapper;
import dev.mathbook3948.scope.facade.GuildThreadEventFacade;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ThreadEventListener extends ListenerAdapter {

    private final GuildThreadEventFacade guildThreadEventFacade;

    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        if (!event.isFromGuild()) return;
        if (!(event.getChannel() instanceof ThreadChannel thread)) return;

        guildThreadEventFacade.onThreadCreate(JdaMapper.toThreadEventInfo(thread));
    }

    @Override
    public void onChannelDelete(ChannelDeleteEvent event) {
        if (!event.isFromGuild()) return;
        if (!(event.getChannel() instanceof ThreadChannel thread)) return;

        guildThreadEventFacade.onThreadDelete(JdaMapper.toThreadEventInfo(thread));
    }
}
