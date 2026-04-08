package dev.mathbook3948.scope.discord.listener;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoiceEventListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() != null && event.getChannelLeft() == null) {
            // TODO: facade에 위임 — 음성 입장 기록
        } else if (event.getChannelLeft() != null && event.getChannelJoined() == null) {
            // TODO: facade에 위임 — 음성 퇴장 기록, 체류 시간 계산
        } else {
            // TODO: facade에 위임 — 채널 이동 기록
        }
    }
}
