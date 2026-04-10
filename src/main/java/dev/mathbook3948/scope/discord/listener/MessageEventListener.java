package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.facade.MessageEventFacade;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageEventListener extends ListenerAdapter {

    private final MessageEventFacade messageEventFacade;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild()) return;
        if (event.getAuthor().isBot()) return;

        messageEventFacade.onMessageSend(
            event.getGuild().getIdLong(),
            event.getChannel().getIdLong(),
            event.getAuthor().getIdLong(),
            event.getMessageIdLong(),
            event.getMessage().getContentRaw().length()
        );
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        if (!event.isFromGuild()) return;

        messageEventFacade.onMessageDelete(
            event.getGuild().getIdLong(),
            event.getChannel().getIdLong(),
            event.getMessageIdLong()
        );
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (!event.isFromGuild()) return;
        if (event.getAuthor().isBot()) return;

        messageEventFacade.onMessageUpdate(
            event.getGuild().getIdLong(),
            event.getChannel().getIdLong(),
            event.getAuthor().getIdLong(),
            event.getMessageIdLong(),
            event.getMessage().getContentRaw().length()
        );
    }
}
