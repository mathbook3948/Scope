package dev.mathbook3948.scope.domain.guild.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import dev.mathbook3948.scope.domain.guild.Guild;
import dev.mathbook3948.scope.domain.guild.GuildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildChannelService {

    private final GuildChannelRepository guildChannelRepository;
    private final GuildRepository guildRepository;

    @Transactional
    public void upsertGuildChannel(Long guildId, GuildChannelInfo info) {
        guildChannelRepository.findByGuild_GuildIdAndChannelId(guildId, info.channelId())
            .ifPresentOrElse(
                channel -> {
                    channel.updateName(info.name());
                    channel.updateChannelType(info.channelType());
                    channel.updateParentChannelId(info.parentChannelId());
                    channel.updatePosition(info.position());
                },
                () -> guildChannelRepository.save(
                    GuildChannel.of(guildRepository.getReferenceById(guildId), info.channelId(), info.name(), info.channelType(), info.parentChannelId(), info.position())
                )
            );
    }

    @Transactional
    public void upsertGuildChannels(Long guildId, List<GuildChannelInfo> channels) {
        List<Long> channelIds = channels.stream().map(GuildChannelInfo::channelId).toList();

        Map<Long, GuildChannel> existingMap = guildChannelRepository
            .findByGuild_GuildIdAndChannelIdIn(guildId, channelIds)
            .stream()
            .collect(Collectors.toMap(GuildChannel::getChannelId, Function.identity()));

        Guild guildRef = guildRepository.getReferenceById(guildId);
        List<GuildChannel> newChannels = new ArrayList<>();

        for (GuildChannelInfo info : channels) {
            GuildChannel existing = existingMap.get(info.channelId());
            if (existing != null) {
                if (!existing.getName().equals(info.name())) {
                    existing.updateName(info.name());
                }
                if (existing.getChannelType() != info.channelType()) {
                    existing.updateChannelType(info.channelType());
                }
                if (!java.util.Objects.equals(existing.getParentChannelId(), info.parentChannelId())) {
                    existing.updateParentChannelId(info.parentChannelId());
                }
                if (!java.util.Objects.equals(existing.getPosition(), info.position())) {
                    existing.updatePosition(info.position());
                }
            } else {
                newChannels.add(GuildChannel.of(guildRef, info.channelId(), info.name(), info.channelType(), info.parentChannelId(), info.position()));
            }
        }

        if (!newChannels.isEmpty()) {
            guildChannelRepository.saveAll(newChannels);
        }
    }

    @Transactional
    public void deleteGuildChannel(Long guildId, Long channelId) {
        guildChannelRepository.deleteByGuild_GuildIdAndChannelId(guildId, channelId);
    }

    @Transactional
    public void deleteAllByGuildId(Long guildId) {
        guildChannelRepository.deleteAllByGuild_GuildId(guildId);
    }
}
