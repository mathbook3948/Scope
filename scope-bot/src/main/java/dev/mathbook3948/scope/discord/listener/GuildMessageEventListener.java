package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.discord.utils.JdaMapper;
import dev.mathbook3948.scope.facade.GuildMessageEventFacade;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GuildMessageEventListener extends ListenerAdapter {

    private final GuildMessageEventFacade guildMessageEventFacade;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild()) return;
        guildMessageEventFacade.onMessageSend(JdaMapper.toMessageEventInfo(event));
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        if (!event.isFromGuild()) return;
        guildMessageEventFacade.onMessageDelete(JdaMapper.toMessageDeleteInfo(event));
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (!event.isFromGuild()) return;
        guildMessageEventFacade.onMessageUpdate(JdaMapper.toMessageEventInfo(event));
    }
}
