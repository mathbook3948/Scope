package dev.mathbook3948.scope.discord.listener;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageEventListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        // TODO: facade에 위임 — 메시지 활동 수집, 참여도 스코어 갱신
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        // TODO: facade에 위임 — 삭제 메시지 기록
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (event.getAuthor().isBot()) return;
        // TODO: facade에 위임 — 수정 메시지 기록
    }
}
