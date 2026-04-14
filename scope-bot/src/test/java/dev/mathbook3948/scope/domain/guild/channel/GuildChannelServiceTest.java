package dev.mathbook3948.scope.domain.guild.channel;

import dev.mathbook3948.scope.domain.guild.Guild;
import dev.mathbook3948.scope.domain.guild.GuildRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuildChannelServiceTest {

    @InjectMocks
    GuildChannelService guildChannelService;

    @Mock
    GuildChannelRepository guildChannelRepository;

    @Mock
    GuildRepository guildRepository;

    @Test
    @DisplayName("기존 채널은 변경된 필드만 업데이트하고 신규 채널은 저장한다")
    void upsertGuildChannels_mixedExistingAndNew_updatesExistingAndSavesNew() {
        // given
        Long guildId = 1L;
        Guild guildRef = Guild.builder().guildId(guildId).name("TestGuild").build();
        GuildChannel existing = GuildChannel.builder()
            .guild(guildRef)
            .channelId(100L)
            .name("OldName")
            .channelType(GuildChannelType.TEXT)
            .position(0)
            .build();

        when(guildChannelRepository.findByGuild_GuildIdAndChannelIdIn(guildId, List.of(100L, 200L)))
            .thenReturn(List.of(existing));
        when(guildRepository.getReferenceById(guildId)).thenReturn(guildRef);

        List<GuildChannelInfo> channels = List.of(
            new GuildChannelInfo(100L, "NewName", GuildChannelType.VOICE, 999L, 5),
            new GuildChannelInfo(200L, "BrandNew", GuildChannelType.TEXT, null, 1)
        );

        // when
        guildChannelService.upsertGuildChannels(guildId, channels);

        // then
        assertThat(existing.getName()).isEqualTo("NewName");
        assertThat(existing.getChannelType()).isEqualTo(GuildChannelType.VOICE);
        assertThat(existing.getParentChannelId()).isEqualTo(999L);
        assertThat(existing.getPosition()).isEqualTo(5);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<GuildChannel>> captor = ArgumentCaptor.forClass(List.class);
        verify(guildChannelRepository).saveAll(captor.capture());

        List<GuildChannel> saved = captor.getValue();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getChannelId()).isEqualTo(200L);
    }

    @Test
    @DisplayName("일부 필드만 변경되면 해당 필드만 업데이트한다")
    void upsertGuildChannels_partialFieldChange_updatesOnlyChangedFields() {
        // given
        Long guildId = 1L;
        Guild guildRef = Guild.builder().guildId(guildId).name("TestGuild").build();
        GuildChannel existing = GuildChannel.builder()
            .guild(guildRef)
            .channelId(100L)
            .name("General")
            .channelType(GuildChannelType.TEXT)
            .parentChannelId(50L)
            .position(3)
            .build();

        when(guildChannelRepository.findByGuild_GuildIdAndChannelIdIn(guildId, List.of(100L)))
            .thenReturn(List.of(existing));

        // name만 변경, 나머지 동일
        List<GuildChannelInfo> channels = List.of(
            new GuildChannelInfo(100L, "Renamed", GuildChannelType.TEXT, 50L, 3)
        );

        // when
        guildChannelService.upsertGuildChannels(guildId, channels);

        // then
        assertThat(existing.getName()).isEqualTo("Renamed");
        assertThat(existing.getChannelType()).isEqualTo(GuildChannelType.TEXT);
        assertThat(existing.getParentChannelId()).isEqualTo(50L);
        assertThat(existing.getPosition()).isEqualTo(3);
        verify(guildChannelRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("모두 기존 채널이고 값이 같으면 저장하지 않는다")
    void upsertGuildChannels_allExistingSameValues_noSave() {
        // given
        Long guildId = 1L;
        Guild guildRef = Guild.builder().guildId(guildId).name("TestGuild").build();
        GuildChannel existing = GuildChannel.builder()
            .guild(guildRef)
            .channelId(100L)
            .name("General")
            .channelType(GuildChannelType.TEXT)
            .parentChannelId(50L)
            .position(3)
            .build();

        when(guildChannelRepository.findByGuild_GuildIdAndChannelIdIn(guildId, List.of(100L)))
            .thenReturn(List.of(existing));

        // when
        guildChannelService.upsertGuildChannels(guildId, List.of(
            new GuildChannelInfo(100L, "General", GuildChannelType.TEXT, 50L, 3)
        ));

        // then
        verify(guildChannelRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("모두 신규 채널이면 전부 저장한다")
    void upsertGuildChannels_allNew_savesAll() {
        // given
        Long guildId = 1L;
        Guild guildRef = Guild.builder().guildId(guildId).name("TestGuild").build();

        when(guildChannelRepository.findByGuild_GuildIdAndChannelIdIn(guildId, List.of(100L, 200L)))
            .thenReturn(List.of());
        when(guildRepository.getReferenceById(guildId)).thenReturn(guildRef);

        List<GuildChannelInfo> channels = List.of(
            new GuildChannelInfo(100L, "Channel1", GuildChannelType.TEXT, null, 0),
            new GuildChannelInfo(200L, "Channel2", GuildChannelType.VOICE, null, 1)
        );

        // when
        guildChannelService.upsertGuildChannels(guildId, channels);

        // then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<GuildChannel>> captor = ArgumentCaptor.forClass(List.class);
        verify(guildChannelRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }
}
