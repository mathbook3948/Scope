package dev.mathbook3948.scope.discord.listener;

import dev.mathbook3948.scope.domain.guild.reaction.GuildReactionEventInfo;
import dev.mathbook3948.scope.facade.GuildReactionEventFacade;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReactionEventListenerTest {

    @InjectMocks
    ReactionEventListener reactionEventListener;

    @Mock
    GuildReactionEventFacade guildReactionEventFacade;

    @Test
    @DisplayName("onMessageReactionAdd는 길드 이벤트면 Facade.onReactionAdd로 위임한다")
    void onMessageReactionAdd_fromGuild_delegatesToFacade() {
        // given
        MessageReactionAddEvent event = mock(MessageReactionAddEvent.class);
        when(event.isFromGuild()).thenReturn(true);
        stubCommonReactionFields(event, 1L, 10L, 100L, 200L, "smile");
        User user = userBot(false);
        when(event.getUser()).thenReturn(user);

        // when
        reactionEventListener.onMessageReactionAdd(event);

        // then
        ArgumentCaptor<GuildReactionEventInfo> captor = ArgumentCaptor.forClass(GuildReactionEventInfo.class);
        verify(guildReactionEventFacade).onReactionAdd(captor.capture());
        GuildReactionEventInfo value = captor.getValue();
        assertThat(value.guildId()).isEqualTo(1L);
        assertThat(value.channelId()).isEqualTo(10L);
        assertThat(value.messageId()).isEqualTo(100L);
        assertThat(value.memberId()).isEqualTo(200L);
        assertThat(value.emoji()).isEqualTo("smile");
    }

    @Test
    @DisplayName("onMessageReactionAdd는 길드 외 이벤트면 무시한다")
    void onMessageReactionAdd_notFromGuild_skipped() {
        // given
        MessageReactionAddEvent event = mock(MessageReactionAddEvent.class);
        when(event.isFromGuild()).thenReturn(false);

        // when
        reactionEventListener.onMessageReactionAdd(event);

        // then
        verifyNoInteractions(guildReactionEventFacade);
    }

    @Test
    @DisplayName("onMessageReactionRemove는 길드 이벤트면 Facade.onReactionRemove로 위임한다")
    void onMessageReactionRemove_fromGuild_delegatesToFacade() {
        // given
        MessageReactionRemoveEvent event = mock(MessageReactionRemoveEvent.class);
        when(event.isFromGuild()).thenReturn(true);
        stubCommonReactionFields(event, 1L, 10L, 100L, 200L, "heart");
        User user = userBot(false);
        when(event.getUser()).thenReturn(user);

        // when
        reactionEventListener.onMessageReactionRemove(event);

        // then
        ArgumentCaptor<GuildReactionEventInfo> captor = ArgumentCaptor.forClass(GuildReactionEventInfo.class);
        verify(guildReactionEventFacade).onReactionRemove(captor.capture());
        GuildReactionEventInfo value = captor.getValue();
        assertThat(value.guildId()).isEqualTo(1L);
        assertThat(value.channelId()).isEqualTo(10L);
        assertThat(value.messageId()).isEqualTo(100L);
        assertThat(value.memberId()).isEqualTo(200L);
        assertThat(value.emoji()).isEqualTo("heart");
    }

    @Test
    @DisplayName("onMessageReactionRemove는 길드 외 이벤트면 무시한다")
    void onMessageReactionRemove_notFromGuild_skipped() {
        // given
        MessageReactionRemoveEvent event = mock(MessageReactionRemoveEvent.class);
        when(event.isFromGuild()).thenReturn(false);

        // when
        reactionEventListener.onMessageReactionRemove(event);

        // then
        verifyNoInteractions(guildReactionEventFacade);
    }

    private static User userBot(boolean isBot) {
        User user = mock(User.class);
        doReturn(isBot).when(user).isBot();
        doReturn(false).when(user).isSystem();
        return user;
    }

    private static void stubCommonReactionFields(
        net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent event,
        long guildId, long channelId, long messageId, long userId, String emojiCode
    ) {
        Guild guild = mock(Guild.class);
        doReturn(guildId).when(guild).getIdLong();

        MessageChannelUnion channel = mock(MessageChannelUnion.class);
        doReturn(channelId).when(channel).getIdLong();

        EmojiUnion emoji = mock(EmojiUnion.class);
        doReturn(emojiCode).when(emoji).getAsReactionCode();

        MessageReaction reaction = mock(MessageReaction.class);
        doReturn(emoji).when(reaction).getEmoji();

        doReturn(guild).when(event).getGuild();
        doReturn(channel).when(event).getChannel();
        doReturn(messageId).when(event).getMessageIdLong();
        doReturn(userId).when(event).getUserIdLong();
        doReturn(reaction).when(event).getReaction();
    }
}
