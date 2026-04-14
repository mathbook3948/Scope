package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.domain.guild.message.GuildMessageEventInfo;
import dev.mathbook3948.scope.domain.guild.message.GuildMessageSourceType;
import dev.mathbook3948.scope.facade.GuildMessageEventFacade;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.IThreadContainerUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
// updateEvent 헬퍼가 auto-embed 필터에 걸리는 분기에선 Mapper 관련 stub이 쓰이지 않으므로 LENIENT 유지
@MockitoSettings(strictness = Strictness.LENIENT)
class GuildMessageEventListenerTest {

    @InjectMocks
    GuildMessageEventListener guildMessageEventListener;

    @Mock
    GuildMessageEventFacade guildMessageEventFacade;

    @Test
    @DisplayName("onMessageReceived는 길드 메시지면 Facade.onMessageSend로 위임한다")
    void onMessageReceived_fromGuild_delegatesToFacade() {
        // given
        MessageReceivedEvent event = receivedEvent(1L, 10L, 100L, 500L, "hello");

        // when
        guildMessageEventListener.onMessageReceived(event);

        // then
        ArgumentCaptor<GuildMessageEventInfo> captor = ArgumentCaptor.forClass(GuildMessageEventInfo.class);
        verify(guildMessageEventFacade).onMessageSend(captor.capture());
        assertThat(captor.getValue().messageId()).isEqualTo(500L);
    }

    @Test
    @DisplayName("onMessageReceived는 ThreadChannel에서 오면 sourceType이 THREAD로 전달된다")
    void onMessageReceived_fromThreadChannel_sourceTypeThread() {
        // given
        MessageChannelUnion channel = threadChannelUnion(10L, ChannelType.TEXT);
        MessageReceivedEvent event = receivedEventWithChannel(channel, "hi in thread");

        // when
        guildMessageEventListener.onMessageReceived(event);

        // then
        ArgumentCaptor<GuildMessageEventInfo> captor = ArgumentCaptor.forClass(GuildMessageEventInfo.class);
        verify(guildMessageEventFacade).onMessageSend(captor.capture());
        assertThat(captor.getValue().sourceType()).isEqualTo(GuildMessageSourceType.THREAD);
    }

    @Test
    @DisplayName("onMessageReceived는 길드 외 메시지면 무시한다")
    void onMessageReceived_notFromGuild_skipped() {
        // given
        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        when(event.isFromGuild()).thenReturn(false);

        // when
        guildMessageEventListener.onMessageReceived(event);

        // then
        verifyNoInteractions(guildMessageEventFacade);
    }

    @Test
    @DisplayName("onMessageDelete는 길드 외 메시지면 무시한다")
    void onMessageDelete_notFromGuild_skipped() {
        // given
        MessageDeleteEvent event = mock(MessageDeleteEvent.class);
        when(event.isFromGuild()).thenReturn(false);

        // when
        guildMessageEventListener.onMessageDelete(event);

        // then
        verifyNoInteractions(guildMessageEventFacade);
    }

    @Test
    @DisplayName("onMessageDelete는 길드 메시지면 Facade.onMessageDelete로 위임한다")
    void onMessageDelete_fromGuild_delegatesToFacade() {
        // given
        Guild guild = guildWithId(1L);
        MessageChannelUnion channel = channelWithId(10L);
        MessageDeleteEvent event = mock(MessageDeleteEvent.class);
        when(event.isFromGuild()).thenReturn(true);
        when(event.getGuild()).thenReturn(guild);
        when(event.getChannel()).thenReturn(channel);
        when(event.getMessageIdLong()).thenReturn(500L);

        // when
        guildMessageEventListener.onMessageDelete(event);

        // then
        ArgumentCaptor<GuildMessageEventInfo> captor = ArgumentCaptor.forClass(GuildMessageEventInfo.class);
        verify(guildMessageEventFacade).onMessageDelete(captor.capture());
        assertThat(captor.getValue().sourceType()).isEqualTo(GuildMessageSourceType.CHANNEL);
    }

    @Test
    @DisplayName("onMessageDelete는 ThreadChannel에서 오면 sourceType이 THREAD로 전달된다")
    void onMessageDelete_fromThreadChannel_sourceTypeThread() {
        // given
        Guild guild = guildWithId(1L);
        MessageChannelUnion channel = threadChannelUnion(10L, ChannelType.TEXT);
        MessageDeleteEvent event = mock(MessageDeleteEvent.class);
        when(event.isFromGuild()).thenReturn(true);
        when(event.getGuild()).thenReturn(guild);
        when(event.getChannel()).thenReturn(channel);
        when(event.getMessageIdLong()).thenReturn(500L);

        // when
        guildMessageEventListener.onMessageDelete(event);

        // then
        ArgumentCaptor<GuildMessageEventInfo> captor = ArgumentCaptor.forClass(GuildMessageEventInfo.class);
        verify(guildMessageEventFacade).onMessageDelete(captor.capture());
        assertThat(captor.getValue().sourceType()).isEqualTo(GuildMessageSourceType.THREAD);
    }

    @Test
    @DisplayName("onMessageUpdate는 편집된 메시지면 Facade.onMessageUpdate로 위임한다")
    void onMessageUpdate_editedMessage_delegatesToFacade() {
        // given
        MessageUpdateEvent event = updateEvent("edited text", /*edited*/ true, /*embeds*/ List.of());

        // when
        guildMessageEventListener.onMessageUpdate(event);

        // then
        verify(guildMessageEventFacade).onMessageUpdate(any(GuildMessageEventInfo.class));
    }

    @Test
    @DisplayName("onMessageUpdate는 편집된 적 없는 URL 포함 메시지에 embed가 붙은 업데이트를 auto-embed로 간주해 무시한다")
    void onMessageUpdate_autoEmbedUpdate_skipped() {
        // given
        MessageEmbed embed = mock(MessageEmbed.class);
        MessageUpdateEvent event = updateEvent("check https://example.com", /*edited*/ false, /*embeds*/ List.of(embed));

        // when
        guildMessageEventListener.onMessageUpdate(event);

        // then
        verifyNoInteractions(guildMessageEventFacade);
    }

    @Test
    @DisplayName("onMessageUpdate는 편집된 URL 포함 메시지는 정상 기록한다")
    void onMessageUpdate_editedUrlMessage_delegatesToFacade() {
        // given
        MessageEmbed embed = mock(MessageEmbed.class);
        MessageUpdateEvent event = updateEvent("check https://example.com", /*edited*/ true, /*embeds*/ List.of(embed));

        // when
        guildMessageEventListener.onMessageUpdate(event);

        // then
        verify(guildMessageEventFacade).onMessageUpdate(any(GuildMessageEventInfo.class));
    }

    @Test
    @DisplayName("onMessageUpdate는 URL은 있지만 embed가 비어있는 업데이트는 정상 기록한다")
    void onMessageUpdate_urlWithoutEmbeds_delegatesToFacade() {
        // given
        MessageUpdateEvent event = updateEvent("check https://example.com", /*edited*/ false, /*embeds*/ List.of());

        // when
        guildMessageEventListener.onMessageUpdate(event);

        // then
        verify(guildMessageEventFacade).onMessageUpdate(any(GuildMessageEventInfo.class));
    }

    @Test
    @DisplayName("onMessageUpdate는 URL이 없으면 편집 여부와 무관하게 기록한다")
    void onMessageUpdate_noUrl_delegatesToFacade() {
        // given
        MessageUpdateEvent event = updateEvent("no link here", /*edited*/ false, /*embeds*/ List.of());

        // when
        guildMessageEventListener.onMessageUpdate(event);

        // then
        verify(guildMessageEventFacade).onMessageUpdate(any(GuildMessageEventInfo.class));
    }

    @Test
    @DisplayName("onMessageUpdate는 ThreadChannel에서 오면 sourceType이 THREAD로 전달된다")
    void onMessageUpdate_fromThreadChannel_sourceTypeThread() {
        // given
        MessageChannelUnion channel = threadChannelUnion(10L, ChannelType.TEXT);
        MessageUpdateEvent event = updateEventWithChannel(channel, "thread edit", /*edited*/ true, /*embeds*/ List.of());

        // when
        guildMessageEventListener.onMessageUpdate(event);

        // then
        ArgumentCaptor<GuildMessageEventInfo> captor = ArgumentCaptor.forClass(GuildMessageEventInfo.class);
        verify(guildMessageEventFacade).onMessageUpdate(captor.capture());
        assertThat(captor.getValue().sourceType()).isEqualTo(GuildMessageSourceType.THREAD);
    }

    @Test
    @DisplayName("onMessageUpdate는 편집된 메시지가 URL 없이 embed만 있어도 auto-embed 필터를 우회해 정상 기록한다")
    void onMessageUpdate_editedWithEmbedButNoUrl_delegatesToFacade() {
        // given
        MessageEmbed embed = mock(MessageEmbed.class);
        MessageUpdateEvent event = updateEvent("no url here", /*edited*/ true, /*embeds*/ List.of(embed));

        // when
        guildMessageEventListener.onMessageUpdate(event);

        // then
        verify(guildMessageEventFacade).onMessageUpdate(any(GuildMessageEventInfo.class));
    }

    @Test
    @DisplayName("onMessageUpdate는 길드 외 메시지면 무시한다")
    void onMessageUpdate_notFromGuild_skipped() {
        // given
        MessageUpdateEvent event = mock(MessageUpdateEvent.class);
        when(event.isFromGuild()).thenReturn(false);

        // when
        guildMessageEventListener.onMessageUpdate(event);

        // then
        verifyNoInteractions(guildMessageEventFacade);
    }

    private static Guild guildWithId(long id) {
        Guild guild = mock(Guild.class);
        doReturn(id).when(guild).getIdLong();
        return guild;
    }

    private static MessageChannelUnion channelWithId(long id) {
        MessageChannelUnion channel = mock(MessageChannelUnion.class);
        doReturn(id).when(channel).getIdLong();
        return channel;
    }

    private static MessageChannelUnion threadChannelUnion(long id, ChannelType parentType) {
        IThreadContainerUnion parent = mock(IThreadContainerUnion.class);
        doReturn(parentType).when(parent).getType();

        MessageChannelUnion channel = mock(MessageChannelUnion.class,
            withSettings().extraInterfaces(ThreadChannel.class));
        doReturn(id).when(channel).getIdLong();
        doReturn(parent).when((ThreadChannel) channel).getParentChannel();
        return channel;
    }

    private static Message basicMessage(String content) {
        Mentions mentions = mock(Mentions.class);
        doReturn(List.of()).when(mentions).getUsers();

        Message message = mock(Message.class);
        doReturn(content).when(message).getContentRaw();
        doReturn(null).when(message).getMessageReference();
        doReturn(mentions).when(message).getMentions();
        doReturn(List.of()).when(message).getAttachments();
        return message;
    }

    private static MessageReceivedEvent receivedEvent(long guildId, long channelId, long authorId, long messageId, String content) {
        User author = mock(User.class);
        doReturn(authorId).when(author).getIdLong();
        doReturn(false).when(author).isBot();
        doReturn(false).when(author).isSystem();

        Message message = basicMessage(content);
        Guild guild = guildWithId(guildId);
        MessageChannelUnion channel = channelWithId(channelId);

        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        doReturn(true).when(event).isFromGuild();
        doReturn(guild).when(event).getGuild();
        doReturn(channel).when(event).getChannel();
        doReturn(messageId).when(event).getMessageIdLong();
        doReturn(message).when(event).getMessage();
        doReturn(author).when(event).getAuthor();
        return event;
    }

    private static MessageReceivedEvent receivedEventWithChannel(MessageChannelUnion channel, String content) {
        User author = mock(User.class);
        doReturn(100L).when(author).getIdLong();
        doReturn(false).when(author).isBot();
        doReturn(false).when(author).isSystem();

        Message message = basicMessage(content);
        Guild guild = guildWithId(1L);

        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        doReturn(true).when(event).isFromGuild();
        doReturn(guild).when(event).getGuild();
        doReturn(channel).when(event).getChannel();
        doReturn(500L).when(event).getMessageIdLong();
        doReturn(message).when(event).getMessage();
        doReturn(author).when(event).getAuthor();
        return event;
    }

    private static MessageUpdateEvent updateEvent(String content, boolean edited, List<MessageEmbed> embeds) {
        return updateEventWithChannel(channelWithId(10L), content, edited, embeds);
    }

    private static MessageUpdateEvent updateEventWithChannel(MessageChannelUnion channel, String content, boolean edited, List<MessageEmbed> embeds) {
        User author = mock(User.class);
        doReturn(100L).when(author).getIdLong();
        doReturn(false).when(author).isBot();
        doReturn(false).when(author).isSystem();

        Mentions mentions = mock(Mentions.class);
        doReturn(List.of()).when(mentions).getUsers();

        Message message = mock(Message.class);
        doReturn(content).when(message).getContentRaw();
        doReturn(edited).when(message).isEdited();
        doReturn(embeds).when(message).getEmbeds();
        doReturn(null).when(message).getMessageReference();
        doReturn(mentions).when(message).getMentions();
        doReturn(List.of()).when(message).getAttachments();

        Guild guild = guildWithId(1L);

        MessageUpdateEvent event = mock(MessageUpdateEvent.class);
        doReturn(true).when(event).isFromGuild();
        doReturn(guild).when(event).getGuild();
        doReturn(channel).when(event).getChannel();
        doReturn(500L).when(event).getMessageIdLong();
        doReturn(message).when(event).getMessage();
        doReturn(author).when(event).getAuthor();
        return event;
    }
}
