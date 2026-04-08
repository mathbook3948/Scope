package dev.mathbook3948.scope.discord.listener;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReactionEventListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser() != null && event.getUser().isBot()) return;
        // TODO: facade에 위임 — 리액션 추가 기록 (수신자 참여도 +2점)
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        // TODO: facade에 위임 — 리액션 취소 기록
    }
}
