package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.domain.guild.channel.GuildChannelInfo;
import dev.mathbook3948.scope.domain.guild.channel.GuildChannelType;
import dev.mathbook3948.scope.facade.GuildChannelFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelEventListener extends ListenerAdapter {

    private final GuildChannelFacade guildChannelFacade;

    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        if (!event.isFromGuild()) return;

        guildChannelFacade.upsertChannel(event.getGuild().getIdLong(), toChannelInfo(event.getChannel()));
    }

    @Override
    public void onChannelDelete(ChannelDeleteEvent event) {
        if (!event.isFromGuild()) return;

        guildChannelFacade.deleteChannel(
            event.getGuild().getIdLong(),
            event.getChannel().getIdLong()
        );
    }

    @Override
    public void onChannelUpdateName(ChannelUpdateNameEvent event) {
        if (!event.isFromGuild()) return;

        guildChannelFacade.upsertChannel(event.getGuild().getIdLong(), toChannelInfo(event.getChannel()));
    }

    private GuildChannelInfo toChannelInfo(Channel channel) {
        Long parentId = null;
        if (channel instanceof ICategorizableChannel categorizable && categorizable.getParentCategory() != null) {
            parentId = categorizable.getParentCategory().getIdLong();
        }
        return new GuildChannelInfo(
            channel.getIdLong(),
            channel.getName(),
            GuildChannelType.from(channel.getType().name()),
            parentId
        );
    }
}
