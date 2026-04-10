package dev.mathbook3948.scope.facade;

import dev.mathbook3948.scope.domain.guild.channel.GuildChannelInfo;
import dev.mathbook3948.scope.domain.guild.channel.GuildChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildChannelFacade {

    private final GuildChannelService guildChannelService;

    @Transactional
    public void upsertChannel(Long guildId, GuildChannelInfo channelInfo) {
        guildChannelService.upsertGuildChannel(guildId, channelInfo);
    }

    @Transactional
    public void upsertChannels(Long guildId, List<GuildChannelInfo> channels) {
        guildChannelService.upsertGuildChannels(guildId, channels);
    }

    @Transactional
    public void deleteChannel(Long guildId, Long channelId) {
        guildChannelService.deleteGuildChannel(guildId, channelId);
    }
}
