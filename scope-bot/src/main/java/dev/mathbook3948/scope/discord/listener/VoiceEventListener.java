package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.domain.guild.voice.GuildVoiceEventInfo;
import dev.mathbook3948.scope.facade.GuildVoiceEventFacade;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoiceEventListener extends ListenerAdapter {

    private final GuildVoiceEventFacade guildVoiceEventFacade;

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        Long guildId = event.getGuild().getIdLong();
        Long memberId = event.getMember().getIdLong();

        if (event.getChannelJoined() != null && event.getChannelLeft() == null) {
            GuildVoiceEventInfo info = new GuildVoiceEventInfo(guildId, event.getChannelJoined().getIdLong(), memberId);
            guildVoiceEventFacade.onVoiceJoin(info);
        } else if (event.getChannelLeft() != null && event.getChannelJoined() == null) {
            GuildVoiceEventInfo info = new GuildVoiceEventInfo(guildId, event.getChannelLeft().getIdLong(), memberId);
            guildVoiceEventFacade.onVoiceLeave(info);
        } else {
            GuildVoiceEventInfo info = new GuildVoiceEventInfo(guildId, event.getChannelJoined().getIdLong(), memberId);
            guildVoiceEventFacade.onVoiceMove(info);
        }
    }
}
