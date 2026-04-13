package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.domain.guild.AuthorType;
import dev.mathbook3948.scope.domain.guild.message.GuildMessageEventInfo;
import dev.mathbook3948.scope.domain.guild.message.GuildMessageSourceType;
import dev.mathbook3948.scope.facade.GuildMessageEventFacade;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
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
        User author = event.getAuthor();
        GuildMessageEventInfo info = new GuildMessageEventInfo(
            event.getGuild().getIdLong(),
            event.getChannel().getIdLong(),
            author.getIdLong(),
            event.getMessageIdLong(),
            content.codePointCount(0, content.length()),
            sourceTypeOf(event.getChannel()),
            AuthorType.from(author.isBot(), author.isSystem())
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
            null,
            sourceTypeOf(event.getChannel()),
            AuthorType.UNKNOWN
        );
        guildMessageEventFacade.onMessageDelete(info);
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (!event.isFromGuild()) return;

        String content = event.getMessage().getContentRaw();
        User author = event.getAuthor();
        GuildMessageEventInfo info = new GuildMessageEventInfo(
            event.getGuild().getIdLong(),
            event.getChannel().getIdLong(),
            author.getIdLong(),
            event.getMessageIdLong(),
            content.codePointCount(0, content.length()),
            sourceTypeOf(event.getChannel()),
            AuthorType.from(author.isBot(), author.isSystem())
        );
        guildMessageEventFacade.onMessageUpdate(info);
    }

    private static GuildMessageSourceType sourceTypeOf(MessageChannel channel) {
        if (channel instanceof ThreadChannel thread) {
            return thread.getParentChannel().getType() == ChannelType.FORUM
                ? GuildMessageSourceType.FORUM
                : GuildMessageSourceType.THREAD;
        }
        return GuildMessageSourceType.CHANNEL;
    }
}
