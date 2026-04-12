package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.discord.utils.JdaMapper;
import dev.mathbook3948.scope.facade.GuildChannelFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateParentEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdatePositionEvent;
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
        if (event.getChannel() instanceof ThreadChannel) return;

        guildChannelFacade.upsertChannel(event.getGuild().getIdLong(), JdaMapper.toChannelInfo(event.getChannel()));
    }

    @Override
    public void onChannelDelete(ChannelDeleteEvent event) {
        if (!event.isFromGuild()) return;
        if (event.getChannel() instanceof ThreadChannel) return;

        guildChannelFacade.deleteChannel(
            event.getGuild().getIdLong(),
            event.getChannel().getIdLong()
        );
    }

    @Override
    public void onChannelUpdateName(ChannelUpdateNameEvent event) {
        if (!event.isFromGuild()) return;
        if (event.getChannel() instanceof ThreadChannel) return;

        guildChannelFacade.upsertChannel(event.getGuild().getIdLong(), JdaMapper.toChannelInfo(event.getChannel()));
    }

    @Override
    public void onChannelUpdateParent(ChannelUpdateParentEvent event) {
        if (!event.isFromGuild()) return;
        if (event.getChannel() instanceof ThreadChannel) return;

        guildChannelFacade.upsertChannel(event.getGuild().getIdLong(), JdaMapper.toChannelInfo(event.getChannel()));
    }

    @Override
    public void onChannelUpdatePosition(ChannelUpdatePositionEvent event) {
        if (!event.isFromGuild()) return;
        if (event.getChannel() instanceof ThreadChannel) return;

        guildChannelFacade.upsertChannel(event.getGuild().getIdLong(), JdaMapper.toChannelInfo(event.getChannel()));
    }
}
