package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.domain.guild.message.GuildMessageEventInfo;
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

        String content = event.getMessage().getContentRaw();
        GuildMessageEventInfo info = new GuildMessageEventInfo(
            event.getGuild().getIdLong(),
            event.getChannel().getIdLong(),
            event.getAuthor().getIdLong(),
            event.getMessageIdLong(),
            content.codePointCount(0, content.length())
        );
        guildMessageEventFacade.onMessageSend(info);
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        if (!event.isFromGuild()) return;

        GuildMessageEventInfo info = new GuildMessageEventInfo(
            event.getGuild().getIdLong(),
            event.getChannel().getIdLong(),
            null,
            event.getMessageIdLong(),
            null
        );
        guildMessageEventFacade.onMessageDelete(info);
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (!event.isFromGuild()) return;

        String content = event.getMessage().getContentRaw();
        GuildMessageEventInfo info = new GuildMessageEventInfo(
            event.getGuild().getIdLong(),
            event.getChannel().getIdLong(),
            event.getAuthor().getIdLong(),
            event.getMessageIdLong(),
            content.codePointCount(0, content.length())
        );
        guildMessageEventFacade.onMessageUpdate(info);
    }
}
