package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.domain.guild.voice.GuildVoiceEventInfo;
import dev.mathbook3948.scope.facade.GuildVoiceEventFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoiceEventListener extends ListenerAdapter {

    private final GuildVoiceEventFacade guildVoiceEventFacade;

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        Long guildId = event.getGuild().getIdLong();
        Long memberId = event.getMember().getIdLong();
        var joined = event.getChannelJoined();
        var left = event.getChannelLeft();

        if (joined != null && left == null) {
            GuildVoiceEventInfo info = new GuildVoiceEventInfo(guildId, joined.getIdLong(), memberId);
            guildVoiceEventFacade.onVoiceJoin(info);
        } else if (left != null && joined == null) {
            GuildVoiceEventInfo info = new GuildVoiceEventInfo(guildId, left.getIdLong(), memberId);
            guildVoiceEventFacade.onVoiceLeave(info);
        } else if (joined != null && left != null) {
            GuildVoiceEventInfo joinedInfo = new GuildVoiceEventInfo(guildId, joined.getIdLong(), memberId);
            GuildVoiceEventInfo leftInfo = new GuildVoiceEventInfo(guildId, left.getIdLong(), memberId);
            guildVoiceEventFacade.onVoiceMove(joinedInfo, leftInfo);
        } else {
            log.warn("Unexpected voice update state: both channels null, guildId={}, memberId={}", guildId, memberId);
        }
    }
}
