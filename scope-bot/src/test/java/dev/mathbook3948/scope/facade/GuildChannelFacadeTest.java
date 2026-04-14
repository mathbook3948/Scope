package dev.mathbook3948.scope.facade;

import dev.mathbook3948.scope.domain.guild.channel.GuildChannelInfo;
import dev.mathbook3948.scope.domain.guild.channel.GuildChannelService;
import dev.mathbook3948.scope.domain.guild.channel.GuildChannelType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class GuildChannelFacadeTest {

    @InjectMocks
    GuildChannelFacade guildChannelFacade;

    @Mock
    GuildChannelService guildChannelService;

    @Test
    @DisplayName("upsertChannel은 Service의 upsertGuildChannel로 위임한다")
    void upsertChannel_singleChannel_delegatesToService() {
        // given
        Long guildId = 1L;
        GuildChannelInfo info = new GuildChannelInfo(100L, "general", GuildChannelType.TEXT, null, 0);

        // when
        guildChannelFacade.upsertChannel(guildId, info);

        // then
        verify(guildChannelService).upsertGuildChannel(guildId, info);
        verifyNoMoreInteractions(guildChannelService);
    }

    @Test
    @DisplayName("upsertChannels는 Service의 upsertGuildChannels로 위임한다")
    void upsertChannels_multipleChannels_delegatesToService() {
        // given
        Long guildId = 1L;
        List<GuildChannelInfo> channels = List.of(
            new GuildChannelInfo(100L, "a", GuildChannelType.TEXT, null, 0),
            new GuildChannelInfo(101L, "b", GuildChannelType.VOICE, null, 1)
        );

        // when
        guildChannelFacade.upsertChannels(guildId, channels);

        // then
        verify(guildChannelService).upsertGuildChannels(guildId, channels);
        verifyNoMoreInteractions(guildChannelService);
    }

    // Facade에 빈 리스트 skip 최적화(zero-guard)를 추가하지 않는다는 계약을 잠그는 회귀 테스트
    @Test
    @DisplayName("upsertChannels는 빈 리스트여도 Service로 그대로 위임한다")
    void upsertChannels_emptyList_delegatesToService() {
        // given
        Long guildId = 1L;
        List<GuildChannelInfo> channels = List.of();

        // when
        guildChannelFacade.upsertChannels(guildId, channels);

        // then
        verify(guildChannelService).upsertGuildChannels(guildId, channels);
        verifyNoMoreInteractions(guildChannelService);
    }

    @Test
    @DisplayName("deleteChannel은 Service의 deleteGuildChannel로 위임한다")
    void deleteChannel_byId_delegatesToService() {
        // given
        Long guildId = 1L;
        Long channelId = 100L;

        // when
        guildChannelFacade.deleteChannel(guildId, channelId);

        // then
        verify(guildChannelService).deleteGuildChannel(guildId, channelId);
        verifyNoMoreInteractions(guildChannelService);
    }
}
