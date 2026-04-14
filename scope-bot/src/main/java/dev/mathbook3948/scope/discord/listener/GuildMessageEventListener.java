package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.discord.utils.JdaMapper;
import dev.mathbook3948.scope.utils.CommonUtil;
import dev.mathbook3948.scope.facade.GuildMessageEventFacade;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
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

    /**
     * 메시지 업데이트 이벤트를 처리한다.
     * <p>Discord는 본문에 URL이 포함된 메시지에 auto-embed를 붙이면서 {@link MessageUpdateEvent}를
     * 한 번 더 발행하는데, 이 경우 유저 편집이 아니므로 기록 대상에서 제외한다.
     */
    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (!event.isFromGuild()) return;
        Message message = event.getMessage();
        // 편집된 적 없는데 URL이 포함되고 embed가 붙은 UPDATE는 auto-embed로 간주
        if (!message.isEdited() && CommonUtil.hasUrl(message.getContentRaw())
            && !message.getEmbeds().isEmpty()) return;
        guildMessageEventFacade.onMessageUpdate(JdaMapper.toMessageEventInfo(event));
    }
}
