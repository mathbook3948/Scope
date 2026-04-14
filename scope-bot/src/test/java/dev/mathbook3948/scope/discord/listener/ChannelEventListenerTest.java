package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.domain.guild.channel.GuildChannelInfo;
import dev.mathbook3948.scope.domain.guild.thread.GuildThreadEventInfo;
import dev.mathbook3948.scope.facade.GuildChannelFacade;
import dev.mathbook3948.scope.facade.GuildThreadEventFacade;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.ChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.IThreadContainerUnion;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateParentEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdatePositionEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
// JDA 채널 헬퍼가 Mapper 호출 여부에 따라 분기별로 쓰이지 않는 stub을 포함하므로 LENIENT 유지
// 주의: 헬퍼에 stub을 추가할 땐 모든 호출 경로에서 실제로 쓰이는지 직접 확인할 것 — STRICT가 아니므로 죽은 stub이 조용히 누적될 수 있다
@MockitoSettings(strictness = Strictness.LENIENT)
class ChannelEventListenerTest {

    @InjectMocks
    ChannelEventListener channelEventListener;

    @Mock
    GuildChannelFacade guildChannelFacade;

    @Mock
    GuildThreadEventFacade guildThreadEventFacade;

    @Test
    @DisplayName("onChannelCreate는 ThreadChannel이면 ThreadEventFacade로 위임하고 모든 필드를 전달한다")
    void onChannelCreate_threadChannel_delegatesToThreadFacadeWithAllFields() {
        // given
        ChannelUnion channel = threadChannelUnion(1L, 50L, 300L, 777L, "sprint");
        ChannelCreateEvent event = mock(ChannelCreateEvent.class);
        when(event.isFromGuild()).thenReturn(true);
        when(event.getChannel()).thenReturn(channel);

        // when
        channelEventListener.onChannelCreate(event);

        // then
        ArgumentCaptor<GuildThreadEventInfo> captor = ArgumentCaptor.forClass(GuildThreadEventInfo.class);
        verify(guildThreadEventFacade).onThreadCreate(captor.capture());
        GuildThreadEventInfo value = captor.getValue();
        assertThat(value.guildId()).isEqualTo(1L);
        assertThat(value.parentChannelId()).isEqualTo(50L);
        assertThat(value.threadId()).isEqualTo(300L);
        assertThat(value.ownerId()).isEqualTo(777L);
        assertThat(value.name()).isEqualTo("sprint");
        verifyNoInteractions(guildChannelFacade);
    }

    @Test
    @DisplayName("onChannelCreate는 일반 채널이면 ChannelFacade로 upsert한다")
    void onChannelCreate_regularChannel_delegatesToChannelFacade() {
        // given
        ChannelUnion channel = textChannelUnion(100L, "general");
        ChannelCreateEvent event = mock(ChannelCreateEvent.class);
        when(event.isFromGuild()).thenReturn(true);
        when(event.getChannel()).thenReturn(channel);
        Guild guild = guildWithId(1L);
        when(event.getGuild()).thenReturn(guild);

        // when
        channelEventListener.onChannelCreate(event);

        // then
        ArgumentCaptor<GuildChannelInfo> captor = ArgumentCaptor.forClass(GuildChannelInfo.class);
        verify(guildChannelFacade).upsertChannel(eq(1L), captor.capture());
        assertThat(captor.getValue().channelId()).isEqualTo(100L);
        verifyNoInteractions(guildThreadEventFacade);
    }

    @Test
    @DisplayName("onChannelCreate는 길드 외 채널이면 무시한다")
    void onChannelCreate_notFromGuild_noInteractions() {
        // given
        ChannelCreateEvent event = mock(ChannelCreateEvent.class);
        when(event.isFromGuild()).thenReturn(false);

        // when
        channelEventListener.onChannelCreate(event);

        // then
        verifyNoInteractions(guildChannelFacade, guildThreadEventFacade);
    }

    @Test
    @DisplayName("onChannelDelete는 길드 외 채널이면 무시한다")
    void onChannelDelete_notFromGuild_noInteractions() {
        // given
        ChannelDeleteEvent event = mock(ChannelDeleteEvent.class);
        when(event.isFromGuild()).thenReturn(false);

        // when
        channelEventListener.onChannelDelete(event);

        // then
        verifyNoInteractions(guildChannelFacade, guildThreadEventFacade);
    }

    @Test
    @DisplayName("onChannelDelete는 ThreadChannel이면 ThreadEventFacade로 위임하고 모든 필드를 전달한다")
    void onChannelDelete_threadChannel_delegatesToThreadFacadeWithAllFields() {
        // given
        ChannelUnion channel = threadChannelUnion(1L, 50L, 300L, 777L, "sprint");
        ChannelDeleteEvent event = mock(ChannelDeleteEvent.class);
        when(event.isFromGuild()).thenReturn(true);
        when(event.getChannel()).thenReturn(channel);

        // when
        channelEventListener.onChannelDelete(event);

        // then
        ArgumentCaptor<GuildThreadEventInfo> captor = ArgumentCaptor.forClass(GuildThreadEventInfo.class);
        verify(guildThreadEventFacade).onThreadDelete(captor.capture());
        GuildThreadEventInfo value = captor.getValue();
        assertThat(value.guildId()).isEqualTo(1L);
        assertThat(value.parentChannelId()).isEqualTo(50L);
        assertThat(value.threadId()).isEqualTo(300L);
        assertThat(value.ownerId()).isEqualTo(777L);
        assertThat(value.name()).isEqualTo("sprint");
        verifyNoInteractions(guildChannelFacade);
    }

    @Test
    @DisplayName("onChannelDelete는 일반 채널이면 ChannelFacade.deleteChannel로 위임한다")
    void onChannelDelete_regularChannel_delegatesToChannelFacade() {
        // given
        ChannelUnion channel = textChannelUnion(100L, "general");
        ChannelDeleteEvent event = mock(ChannelDeleteEvent.class);
        when(event.isFromGuild()).thenReturn(true);
        when(event.getChannel()).thenReturn(channel);
        Guild guild = guildWithId(1L);
        when(event.getGuild()).thenReturn(guild);

        // when
        channelEventListener.onChannelDelete(event);

        // then
        verify(guildChannelFacade).deleteChannel(1L, 100L);
        verifyNoInteractions(guildThreadEventFacade);
    }

    @Test
    @DisplayName("onChannelUpdateName은 길드 외 채널이면 무시한다")
    void onChannelUpdateName_notFromGuild_noInteractions() {
        // given
        ChannelUpdateNameEvent event = mock(ChannelUpdateNameEvent.class);
        when(event.isFromGuild()).thenReturn(false);

        // when
        channelEventListener.onChannelUpdateName(event);

        // then
        verifyNoInteractions(guildChannelFacade, guildThreadEventFacade);
    }

    @Test
    @DisplayName("onChannelUpdateName은 ThreadChannel이면 무시한다")
    void onChannelUpdateName_threadChannel_skipped() {
        // given
        ChannelUnion channel = mock(ChannelUnion.class, withSettings().extraInterfaces(ThreadChannel.class));
        ChannelUpdateNameEvent event = mock(ChannelUpdateNameEvent.class);
        when(event.isFromGuild()).thenReturn(true);
        when(event.getChannel()).thenReturn(channel);

        // when
        channelEventListener.onChannelUpdateName(event);

        // then
        verifyNoInteractions(guildChannelFacade, guildThreadEventFacade);
    }

    @Test
    @DisplayName("onChannelUpdateName은 일반 채널이면 upsert로 위임한다")
    void onChannelUpdateName_regularChannel_delegatesToChannelFacade() {
        // given
        ChannelUnion channel = textChannelUnion(100L, "renamed");
        ChannelUpdateNameEvent event = mock(ChannelUpdateNameEvent.class);
        when(event.isFromGuild()).thenReturn(true);
        when(event.getChannel()).thenReturn(channel);
        Guild guild = guildWithId(1L);
        when(event.getGuild()).thenReturn(guild);

        // when
        channelEventListener.onChannelUpdateName(event);

        // then
        ArgumentCaptor<GuildChannelInfo> captor = ArgumentCaptor.forClass(GuildChannelInfo.class);
        verify(guildChannelFacade).upsertChannel(eq(1L), captor.capture());
        assertThat(captor.getValue().name()).isEqualTo("renamed");
    }

    @Test
    @DisplayName("onChannelUpdateParent는 길드 외 채널이면 무시한다")
    void onChannelUpdateParent_notFromGuild_noInteractions() {
        // given
        ChannelUpdateParentEvent event = mock(ChannelUpdateParentEvent.class);
        when(event.isFromGuild()).thenReturn(false);

        // when
        channelEventListener.onChannelUpdateParent(event);

        // then
        verifyNoInteractions(guildChannelFacade, guildThreadEventFacade);
    }

    @Test
    @DisplayName("onChannelUpdateParent는 ThreadChannel이면 무시한다")
    void onChannelUpdateParent_threadChannel_skipped() {
        // given
        ChannelUnion channel = mock(ChannelUnion.class, withSettings().extraInterfaces(ThreadChannel.class));
        ChannelUpdateParentEvent event = mock(ChannelUpdateParentEvent.class);
        when(event.isFromGuild()).thenReturn(true);
        when(event.getChannel()).thenReturn(channel);

        // when
        channelEventListener.onChannelUpdateParent(event);

        // then
        verifyNoInteractions(guildChannelFacade, guildThreadEventFacade);
    }

    @Test
    @DisplayName("onChannelUpdateParent는 일반 채널이면 upsert로 위임한다")
    void onChannelUpdateParent_regularChannel_delegatesToChannelFacade() {
        // given
        ChannelUnion channel = textChannelUnion(100L, "moved");
        ChannelUpdateParentEvent event = mock(ChannelUpdateParentEvent.class);
        when(event.isFromGuild()).thenReturn(true);
        when(event.getChannel()).thenReturn(channel);
        Guild guild = guildWithId(1L);
        when(event.getGuild()).thenReturn(guild);

        // when
        channelEventListener.onChannelUpdateParent(event);

        // then
        ArgumentCaptor<GuildChannelInfo> captor = ArgumentCaptor.forClass(GuildChannelInfo.class);
        verify(guildChannelFacade).upsertChannel(eq(1L), captor.capture());
        assertThat(captor.getValue().channelId()).isEqualTo(100L);
        verifyNoInteractions(guildThreadEventFacade);
    }

    @Test
    @DisplayName("onChannelUpdatePosition은 길드 외 채널이면 무시한다")
    void onChannelUpdatePosition_notFromGuild_noInteractions() {
        // given
        ChannelUpdatePositionEvent event = mock(ChannelUpdatePositionEvent.class);
        when(event.isFromGuild()).thenReturn(false);

        // when
        channelEventListener.onChannelUpdatePosition(event);

        // then
        verifyNoInteractions(guildChannelFacade, guildThreadEventFacade);
    }

    @Test
    @DisplayName("onChannelUpdatePosition은 ThreadChannel이면 무시한다")
    void onChannelUpdatePosition_threadChannel_skipped() {
        // given
        ChannelUnion channel = mock(ChannelUnion.class, withSettings().extraInterfaces(ThreadChannel.class));
        ChannelUpdatePositionEvent event = mock(ChannelUpdatePositionEvent.class);
        when(event.isFromGuild()).thenReturn(true);
        when(event.getChannel()).thenReturn(channel);

        // when
        channelEventListener.onChannelUpdatePosition(event);

        // then
        verifyNoInteractions(guildChannelFacade, guildThreadEventFacade);
    }

    @Test
    @DisplayName("onChannelUpdatePosition은 일반 채널이면 upsert로 위임한다")
    void onChannelUpdatePosition_regularChannel_delegatesToChannelFacade() {
        // given
        ChannelUnion channel = textChannelUnion(100L, "general");
        ChannelUpdatePositionEvent event = mock(ChannelUpdatePositionEvent.class);
        when(event.isFromGuild()).thenReturn(true);
        when(event.getChannel()).thenReturn(channel);
        Guild guild = guildWithId(1L);
        when(event.getGuild()).thenReturn(guild);

        // when
        channelEventListener.onChannelUpdatePosition(event);

        // then
        ArgumentCaptor<GuildChannelInfo> captor = ArgumentCaptor.forClass(GuildChannelInfo.class);
        verify(guildChannelFacade).upsertChannel(eq(1L), captor.capture());
        assertThat(captor.getValue().channelId()).isEqualTo(100L);
        verifyNoInteractions(guildThreadEventFacade);
    }

    private static Guild guildWithId(long id) {
        Guild guild = mock(Guild.class);
        doReturn(id).when(guild).getIdLong();
        return guild;
    }

    private static ChannelUnion textChannelUnion(long id, String name) {
        ChannelUnion channel = mock(ChannelUnion.class, withSettings().extraInterfaces(TextChannel.class));
        doReturn(id).when(channel).getIdLong();
        doReturn(name).when(channel).getName();
        doReturn(ChannelType.TEXT).when(channel).getType();
        TextChannel textView = (TextChannel) channel;
        doReturn(null).when(textView).getParentCategory();
        doReturn(0).when(textView).getPosition();
        return channel;
    }

    private static ChannelUnion threadChannelUnion(long guildId, long parentId, long threadId, long ownerId, String name) {
        IThreadContainerUnion parent = mock(IThreadContainerUnion.class);
        doReturn(parentId).when(parent).getIdLong();

        Guild guild = guildWithId(guildId);

        ChannelUnion channel = mock(ChannelUnion.class, withSettings().extraInterfaces(ThreadChannel.class));
        ThreadChannel threadView = (ThreadChannel) channel;
        doReturn(guild).when(threadView).getGuild();
        doReturn(parent).when(threadView).getParentChannel();
        doReturn(threadId).when(threadView).getIdLong();
        doReturn(ownerId).when(threadView).getOwnerIdLong();
        doReturn(name).when(threadView).getName();
        return channel;
    }
}
