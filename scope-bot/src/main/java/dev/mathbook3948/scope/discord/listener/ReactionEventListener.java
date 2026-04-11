package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.discord.utils.JdaMapper;
import dev.mathbook3948.scope.facade.GuildReactionEventFacade;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReactionEventListener extends ListenerAdapter {

    private final GuildReactionEventFacade guildReactionEventFacade;

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.isFromGuild()) return;
        if (event.getUser() != null && event.getUser().isBot()) return;

        guildReactionEventFacade.onReactionAdd(JdaMapper.toReactionEventInfo(event));
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (!event.isFromGuild()) return;
        if (event.getUser() != null && event.getUser().isBot()) return;

        guildReactionEventFacade.onReactionRemove(JdaMapper.toReactionEventInfo(event));
    }
}
