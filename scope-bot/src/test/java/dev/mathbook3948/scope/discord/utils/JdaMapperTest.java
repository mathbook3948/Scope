package dev.mathbook3948.scope.discord.utils;

import dev.mathbook3948.scope.domain.guild.AuthorType;
import dev.mathbook3948.scope.domain.guild.channel.GuildChannelInfo;
import dev.mathbook3948.scope.domain.guild.channel.GuildChannelType;
import dev.mathbook3948.scope.domain.guild.member.GuildMemberInfo;
import dev.mathbook3948.scope.domain.guild.message.GuildMessageEventInfo;
import dev.mathbook3948.scope.domain.guild.message.GuildMessageSourceType;
import dev.mathbook3948.scope.domain.guild.reaction.GuildReactionEventInfo;
import dev.mathbook3948.scope.domain.guild.thread.GuildThreadEventInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.IThreadContainerUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class JdaMapperTest {

    @Test
    @DisplayName("TextChannel은 부모 카테고리 ID와 position을 포함해 변환된다")
    void toChannelInfo_textChannelWithCategory_includesParentIdAndPosition() {
        // given
        Category category = mock(Category.class);
        when(category.getIdLong()).thenReturn(500L);

        TextChannel channel = mock(TextChannel.class);
        when(channel.getIdLong()).thenReturn(100L);
        when(channel.getName()).thenReturn("general");
        when(channel.getType()).thenReturn(ChannelType.TEXT);
        when(channel.getParentCategory()).thenReturn(category);
        when(channel.getPosition()).thenReturn(3);

        // when
        GuildChannelInfo info = JdaMapper.toChannelInfo(channel);

        // then
        assertThat(info.channelId()).isEqualTo(100L);
        assertThat(info.name()).isEqualTo("general");
        assertThat(info.channelType()).isEqualTo(GuildChannelType.TEXT);
        assertThat(info.parentChannelId()).isEqualTo(500L);
        assertThat(info.position()).isEqualTo(3);
    }

    @Test
    @DisplayName("부모 카테고리가 없는 TextChannel은 parentChannelId가 null이다")
    void toChannelInfo_textChannelWithoutCategory_nullParentId() {
        // given
        TextChannel channel = mock(TextChannel.class);
        when(channel.getIdLong()).thenReturn(101L);
        when(channel.getName()).thenReturn("lobby");
        when(channel.getType()).thenReturn(ChannelType.TEXT);
        when(channel.getParentCategory()).thenReturn(null);
        when(channel.getPosition()).thenReturn(0);

        // when
        GuildChannelInfo info = JdaMapper.toChannelInfo(channel);

        // then
        assertThat(info.parentChannelId()).isNull();
        assertThat(info.position()).isEqualTo(0);
    }

    @Test
    @DisplayName("Category 자체는 categorizable 아니므로 parentChannelId가 null이다")
    void toChannelInfo_category_nullParentId() {
        // given
        Category category = mock(Category.class);
        when(category.getIdLong()).thenReturn(200L);
        when(category.getName()).thenReturn("Games");
        when(category.getType()).thenReturn(ChannelType.CATEGORY);
        when(category.getPosition()).thenReturn(1);

        // when
        GuildChannelInfo info = JdaMapper.toChannelInfo(category);

        // then
        assertThat(info.channelId()).isEqualTo(200L);
        assertThat(info.channelType()).isEqualTo(GuildChannelType.CATEGORY);
        assertThat(info.parentChannelId()).isNull();
        assertThat(info.position()).isEqualTo(1);
    }

    @Test
    @DisplayName("Member의 기본 필드와 계정 생성 시각을 변환한다")
    void toMemberInfo_validMember_mapsAllFields() {
        // given
        OffsetDateTime createdAt = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        Member member = mock(Member.class);
        when(member.getIdLong()).thenReturn(1001L);
        when(member.getEffectiveName()).thenReturn("nick");
        when(member.getEffectiveAvatarUrl()).thenReturn("https://avatar");
        when(member.getTimeCreated()).thenReturn(createdAt);

        // when
        GuildMemberInfo info = JdaMapper.toMemberInfo(member);

        // then
        assertThat(info.memberId()).isEqualTo(1001L);
        assertThat(info.name()).isEqualTo("nick");
        assertThat(info.avatarUrl()).isEqualTo("https://avatar");
        assertThat(info.accountCreatedAt()).isEqualTo(createdAt.toInstant());
    }

    @Test
    @DisplayName("Reaction 이벤트의 user가 null이면 authorType은 UNKNOWN이다")
    void toReactionEventInfo_nullUser_authorTypeUnknown() {
        // given
        MessageReactionAddEvent event = reactionAddEvent(null, "smile", 1L, 10L, 100L, 200L);

        // when
        GuildReactionEventInfo info = JdaMapper.toReactionEventInfo(event);

        // then
        assertThat(info.authorType()).isEqualTo(AuthorType.UNKNOWN);
        assertThat(info.emoji()).isEqualTo("smile");
        assertThat(info.guildId()).isEqualTo(1L);
        assertThat(info.channelId()).isEqualTo(10L);
        assertThat(info.messageId()).isEqualTo(100L);
        assertThat(info.memberId()).isEqualTo(200L);
    }

    @Test
    @DisplayName("Reaction 이벤트의 user가 bot이면 authorType은 BOT이다")
    void toReactionEventInfo_botUser_authorTypeBot() {
        // given
        User user = mock(User.class);
        when(user.isBot()).thenReturn(true);
        when(user.isSystem()).thenReturn(false);
        MessageReactionAddEvent event = reactionAddEvent(user, "heart", 1L, 10L, 100L, 200L);

        // when
        GuildReactionEventInfo info = JdaMapper.toReactionEventInfo(event);

        // then
        assertThat(info.authorType()).isEqualTo(AuthorType.BOT);
    }

    @Test
    @DisplayName("Reaction 이벤트의 user가 system이면 authorType은 SYSTEM이다")
    void toReactionEventInfo_systemUser_authorTypeSystem() {
        // given
        User user = mock(User.class);
        when(user.isBot()).thenReturn(false);
        when(user.isSystem()).thenReturn(true);
        MessageReactionAddEvent event = reactionAddEvent(user, "star", 1L, 10L, 100L, 200L);

        // when
        GuildReactionEventInfo info = JdaMapper.toReactionEventInfo(event);

        // then
        assertThat(info.authorType()).isEqualTo(AuthorType.SYSTEM);
    }

    @Test
    @DisplayName("MessageReceived 이벤트는 멘션/첨부/링크/reply/sourceType을 포함해 변환된다")
    void toMessageEventInfo_receivedWithReplyAndMentions_mapsAllFields() {
        // given
        String content = "안녕 https://example.com";
        User author = mock(User.class);
        when(author.getIdLong()).thenReturn(100L);
        when(author.isBot()).thenReturn(false);
        when(author.isSystem()).thenReturn(false);

        MessageReference ref = mock(MessageReference.class);
        when(ref.getMessageIdLong()).thenReturn(999L);

        Mentions mentions = mock(Mentions.class);
        when(mentions.getUsers()).thenReturn(List.of(mock(User.class), mock(User.class)));

        Message message = mock(Message.class);
        when(message.getContentRaw()).thenReturn(content);
        when(message.getMessageReference()).thenReturn(ref);
        when(message.getMentions()).thenReturn(mentions);
        when(message.getAttachments()).thenReturn(List.of());

        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        Guild guild = guildWithId(1L);
        MessageChannelUnion eventChannel = channelWithId(10L);
        when(event.getGuild()).thenReturn(guild);
        when(event.getChannel()).thenReturn(eventChannel);
        when(event.getMessageIdLong()).thenReturn(500L);
        when(event.getMessage()).thenReturn(message);
        when(event.getAuthor()).thenReturn(author);

        // when
        GuildMessageEventInfo info = JdaMapper.toMessageEventInfo(event);

        // then
        assertThat(info.guildId()).isEqualTo(1L);
        assertThat(info.channelId()).isEqualTo(10L);
        assertThat(info.memberId()).isEqualTo(100L);
        assertThat(info.messageId()).isEqualTo(500L);
        assertThat(info.replyToMessageId()).isEqualTo(999L);
        assertThat(info.contentLength()).isEqualTo(content.codePointCount(0, content.length()));
        assertThat(info.mentionCount()).isEqualTo(2);
        assertThat(info.attachmentCount()).isEqualTo(0);
        assertThat(info.hasLink()).isTrue();
        assertThat(info.sourceType()).isEqualTo(GuildMessageSourceType.CHANNEL);
        assertThat(info.authorType()).isEqualTo(AuthorType.USER);
    }

    @Test
    @DisplayName("MessageReference의 messageId가 0이면 replyToMessageId는 null로 정규화된다")
    void toMessageEventInfo_referenceWithZeroId_replyIdNormalizedToNull() {
        // given
        User author = mock(User.class);
        when(author.getIdLong()).thenReturn(100L);

        MessageReference ref = mock(MessageReference.class);
        when(ref.getMessageIdLong()).thenReturn(0L);

        Mentions mentions = mock(Mentions.class);
        when(mentions.getUsers()).thenReturn(List.of());

        Message message = mock(Message.class);
        when(message.getContentRaw()).thenReturn("hi");
        when(message.getMessageReference()).thenReturn(ref);
        when(message.getMentions()).thenReturn(mentions);
        when(message.getAttachments()).thenReturn(List.of());

        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        Guild guild = guildWithId(1L);
        MessageChannelUnion eventChannel = channelWithId(10L);
        when(event.getGuild()).thenReturn(guild);
        when(event.getChannel()).thenReturn(eventChannel);
        when(event.getMessageIdLong()).thenReturn(500L);
        when(event.getMessage()).thenReturn(message);
        when(event.getAuthor()).thenReturn(author);

        // when
        GuildMessageEventInfo info = JdaMapper.toMessageEventInfo(event);

        // then
        assertThat(info.replyToMessageId()).isNull();
        assertThat(info.hasLink()).isFalse();
    }

    @Test
    @DisplayName("MessageReceived 이벤트의 작성자가 bot이면 authorType은 BOT이다")
    void toMessageEventInfo_receivedByBot_authorTypeBot() {
        // given
        User author = mock(User.class);
        when(author.getIdLong()).thenReturn(100L);
        when(author.isBot()).thenReturn(true);
        when(author.isSystem()).thenReturn(false);

        Mentions mentions = mock(Mentions.class);
        when(mentions.getUsers()).thenReturn(List.of());

        Message message = mock(Message.class);
        when(message.getContentRaw()).thenReturn("bot says hi");
        when(message.getMessageReference()).thenReturn(null);
        when(message.getMentions()).thenReturn(mentions);
        when(message.getAttachments()).thenReturn(List.of());

        Guild guild = guildWithId(1L);
        MessageChannelUnion eventChannel = channelWithId(10L);
        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        when(event.getGuild()).thenReturn(guild);
        when(event.getChannel()).thenReturn(eventChannel);
        when(event.getMessageIdLong()).thenReturn(500L);
        when(event.getMessage()).thenReturn(message);
        when(event.getAuthor()).thenReturn(author);

        // when
        GuildMessageEventInfo info = JdaMapper.toMessageEventInfo(event);

        // then
        assertThat(info.authorType()).isEqualTo(AuthorType.BOT);
    }

    @Test
    @DisplayName("MessageReceived 이벤트의 작성자가 system이면 authorType은 SYSTEM이다")
    void toMessageEventInfo_receivedBySystem_authorTypeSystem() {
        // given
        User author = mock(User.class);
        when(author.getIdLong()).thenReturn(100L);
        when(author.isBot()).thenReturn(false);
        when(author.isSystem()).thenReturn(true);

        Mentions mentions = mock(Mentions.class);
        when(mentions.getUsers()).thenReturn(List.of());

        Message message = mock(Message.class);
        when(message.getContentRaw()).thenReturn("system notice");
        when(message.getMessageReference()).thenReturn(null);
        when(message.getMentions()).thenReturn(mentions);
        when(message.getAttachments()).thenReturn(List.of());

        Guild guild = guildWithId(1L);
        MessageChannelUnion eventChannel = channelWithId(10L);
        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        when(event.getGuild()).thenReturn(guild);
        when(event.getChannel()).thenReturn(eventChannel);
        when(event.getMessageIdLong()).thenReturn(500L);
        when(event.getMessage()).thenReturn(message);
        when(event.getAuthor()).thenReturn(author);

        // when
        GuildMessageEventInfo info = JdaMapper.toMessageEventInfo(event);

        // then
        assertThat(info.authorType()).isEqualTo(AuthorType.SYSTEM);
    }

    @Test
    @DisplayName("MessageUpdate 이벤트도 동일한 스키마로 변환된다")
    void toMessageEventInfo_update_mapsAllFields() {
        // given
        User author = mock(User.class);
        when(author.getIdLong()).thenReturn(100L);
        when(author.isBot()).thenReturn(false);
        when(author.isSystem()).thenReturn(false);

        Mentions mentions = mock(Mentions.class);
        when(mentions.getUsers()).thenReturn(List.of(mock(User.class)));

        Message message = mock(Message.class);
        when(message.getContentRaw()).thenReturn("edited");
        when(message.getMessageReference()).thenReturn(null);
        when(message.getMentions()).thenReturn(mentions);
        when(message.getAttachments()).thenReturn(List.of());

        MessageUpdateEvent event = mock(MessageUpdateEvent.class);
        Guild guild = guildWithId(1L);
        MessageChannelUnion eventChannel = channelWithId(10L);
        when(event.getGuild()).thenReturn(guild);
        when(event.getChannel()).thenReturn(eventChannel);
        when(event.getMessageIdLong()).thenReturn(500L);
        when(event.getMessage()).thenReturn(message);
        when(event.getAuthor()).thenReturn(author);

        // when
        GuildMessageEventInfo info = JdaMapper.toMessageEventInfo(event);

        // then
        assertThat(info.guildId()).isEqualTo(1L);
        assertThat(info.channelId()).isEqualTo(10L);
        assertThat(info.memberId()).isEqualTo(100L);
        assertThat(info.messageId()).isEqualTo(500L);
        assertThat(info.contentLength()).isEqualTo(6);
        assertThat(info.replyToMessageId()).isNull();
        assertThat(info.mentionCount()).isEqualTo(1);
        assertThat(info.attachmentCount()).isEqualTo(0);
        assertThat(info.hasLink()).isFalse();
        assertThat(info.sourceType()).isEqualTo(GuildMessageSourceType.CHANNEL);
        assertThat(info.authorType()).isEqualTo(AuthorType.USER);
    }

    @Test
    @DisplayName("MessageReceived가 포럼 부모를 가진 Thread 채널에서 오면 sourceType은 FORUM이다")
    void toMessageEventInfo_receivedFromForumThread_sourceTypeForum() {
        // given
        User author = mock(User.class);
        when(author.getIdLong()).thenReturn(100L);
        when(author.isBot()).thenReturn(false);
        when(author.isSystem()).thenReturn(false);

        Mentions mentions = mock(Mentions.class);
        when(mentions.getUsers()).thenReturn(List.of());

        Message message = mock(Message.class);
        when(message.getContentRaw()).thenReturn("post body");
        when(message.getMessageReference()).thenReturn(null);
        when(message.getMentions()).thenReturn(mentions);
        when(message.getAttachments()).thenReturn(List.of());

        Guild guild = guildWithId(1L);
        MessageChannelUnion channel = threadChannel(10L, ChannelType.FORUM);

        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        when(event.getGuild()).thenReturn(guild);
        when(event.getChannel()).thenReturn(channel);
        when(event.getMessageIdLong()).thenReturn(500L);
        when(event.getMessage()).thenReturn(message);
        when(event.getAuthor()).thenReturn(author);

        // when
        GuildMessageEventInfo info = JdaMapper.toMessageEventInfo(event);

        // then
        assertThat(info.sourceType()).isEqualTo(GuildMessageSourceType.FORUM);
    }

    @Test
    @DisplayName("MessageReceived가 텍스트 부모를 가진 Thread 채널에서 오면 sourceType은 THREAD이다")
    void toMessageEventInfo_receivedFromTextThread_sourceTypeThread() {
        // given
        User author = mock(User.class);
        when(author.getIdLong()).thenReturn(100L);
        when(author.isBot()).thenReturn(false);
        when(author.isSystem()).thenReturn(false);

        Mentions mentions = mock(Mentions.class);
        when(mentions.getUsers()).thenReturn(List.of());

        Message message = mock(Message.class);
        when(message.getContentRaw()).thenReturn("thread reply");
        when(message.getMessageReference()).thenReturn(null);
        when(message.getMentions()).thenReturn(mentions);
        when(message.getAttachments()).thenReturn(List.of());

        Guild guild = guildWithId(1L);
        MessageChannelUnion channel = threadChannel(10L, ChannelType.TEXT);

        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        when(event.getGuild()).thenReturn(guild);
        when(event.getChannel()).thenReturn(channel);
        when(event.getMessageIdLong()).thenReturn(500L);
        when(event.getMessage()).thenReturn(message);
        when(event.getAuthor()).thenReturn(author);

        // when
        GuildMessageEventInfo info = JdaMapper.toMessageEventInfo(event);

        // then
        assertThat(info.sourceType()).isEqualTo(GuildMessageSourceType.THREAD);
    }

    @Test
    @DisplayName("MessageUpdate가 포럼 부모를 가진 Thread 채널에서 오면 sourceType은 FORUM이다")
    void toMessageEventInfo_updatedFromForumThread_sourceTypeForum() {
        // given
        User author = mock(User.class);
        when(author.getIdLong()).thenReturn(100L);
        when(author.isBot()).thenReturn(false);
        when(author.isSystem()).thenReturn(false);

        Mentions mentions = mock(Mentions.class);
        when(mentions.getUsers()).thenReturn(List.of());

        Message message = mock(Message.class);
        when(message.getContentRaw()).thenReturn("edited post");
        when(message.getMessageReference()).thenReturn(null);
        when(message.getMentions()).thenReturn(mentions);
        when(message.getAttachments()).thenReturn(List.of());

        Guild guild = guildWithId(1L);
        MessageChannelUnion channel = threadChannel(10L, ChannelType.FORUM);

        MessageUpdateEvent event = mock(MessageUpdateEvent.class);
        when(event.getGuild()).thenReturn(guild);
        when(event.getChannel()).thenReturn(channel);
        when(event.getMessageIdLong()).thenReturn(500L);
        when(event.getMessage()).thenReturn(message);
        when(event.getAuthor()).thenReturn(author);

        // when
        GuildMessageEventInfo info = JdaMapper.toMessageEventInfo(event);

        // then
        assertThat(info.sourceType()).isEqualTo(GuildMessageSourceType.FORUM);
    }

    @Test
    @DisplayName("MessageDelete 이벤트는 작성자 정보 없이 UNKNOWN 타입으로 변환된다")
    void toMessageDeleteInfo_basicDelete_mapsIdOnlyFields() {
        // given
        MessageDeleteEvent event = mock(MessageDeleteEvent.class);
        Guild guild = guildWithId(1L);
        MessageChannelUnion eventChannel = channelWithId(10L);
        when(event.getGuild()).thenReturn(guild);
        when(event.getChannel()).thenReturn(eventChannel);
        when(event.getMessageIdLong()).thenReturn(500L);

        // when
        GuildMessageEventInfo info = JdaMapper.toMessageDeleteInfo(event);

        // then
        assertThat(info.guildId()).isEqualTo(1L);
        assertThat(info.channelId()).isEqualTo(10L);
        assertThat(info.messageId()).isEqualTo(500L);
        assertThat(info.memberId()).isNull();
        assertThat(info.contentLength()).isNull();
        assertThat(info.mentionCount()).isNull();
        assertThat(info.attachmentCount()).isNull();
        assertThat(info.hasLink()).isNull();
        assertThat(info.replyToMessageId()).isNull();
        assertThat(info.sourceType()).isEqualTo(GuildMessageSourceType.CHANNEL);
        assertThat(info.authorType()).isEqualTo(AuthorType.UNKNOWN);
    }

    @Test
    @DisplayName("포럼 부모를 가진 Thread 채널에서 발생한 메시지는 FORUM 출처로 분류된다")
    void toMessageDeleteInfo_threadInForum_sourceTypeForum() {
        // given
        IThreadContainerUnion parent = mock(IThreadContainerUnion.class);
        when(parent.getType()).thenReturn(ChannelType.FORUM);

        MessageChannelUnion channel = mock(MessageChannelUnion.class,
            withSettings().extraInterfaces(ThreadChannel.class));
        when(channel.getIdLong()).thenReturn(10L);
        when(((ThreadChannel) channel).getParentChannel()).thenReturn(parent);

        Guild guild = guildWithId(1L);
        MessageDeleteEvent event = mock(MessageDeleteEvent.class);
        when(event.getGuild()).thenReturn(guild);
        when(event.getChannel()).thenReturn(channel);
        when(event.getMessageIdLong()).thenReturn(500L);

        // when
        GuildMessageEventInfo info = JdaMapper.toMessageDeleteInfo(event);

        // then
        assertThat(info.guildId()).isEqualTo(1L);
        assertThat(info.channelId()).isEqualTo(10L);
        assertThat(info.messageId()).isEqualTo(500L);
        assertThat(info.sourceType()).isEqualTo(GuildMessageSourceType.FORUM);
    }

    @Test
    @DisplayName("포럼이 아닌 부모를 가진 Thread 채널에서 발생한 메시지는 THREAD 출처로 분류된다")
    void toMessageDeleteInfo_threadInText_sourceTypeThread() {
        // given
        IThreadContainerUnion parent = mock(IThreadContainerUnion.class);
        when(parent.getType()).thenReturn(ChannelType.TEXT);

        MessageChannelUnion channel = mock(MessageChannelUnion.class,
            withSettings().extraInterfaces(ThreadChannel.class));
        when(channel.getIdLong()).thenReturn(10L);
        when(((ThreadChannel) channel).getParentChannel()).thenReturn(parent);

        Guild guild = guildWithId(1L);
        MessageDeleteEvent event = mock(MessageDeleteEvent.class);
        when(event.getGuild()).thenReturn(guild);
        when(event.getChannel()).thenReturn(channel);
        when(event.getMessageIdLong()).thenReturn(500L);

        // when
        GuildMessageEventInfo info = JdaMapper.toMessageDeleteInfo(event);

        // then
        assertThat(info.guildId()).isEqualTo(1L);
        assertThat(info.channelId()).isEqualTo(10L);
        assertThat(info.messageId()).isEqualTo(500L);
        assertThat(info.sourceType()).isEqualTo(GuildMessageSourceType.THREAD);
    }

    @Test
    @DisplayName("ThreadChannel은 부모 채널 ID와 owner, 이름을 포함해 변환된다")
    void toThreadEventInfo_validThread_mapsAllFields() {
        // given
        IThreadContainerUnion parent = mock(IThreadContainerUnion.class);
        when(parent.getIdLong()).thenReturn(50L);

        Guild guild = guildWithId(1L);
        ThreadChannel thread = mock(ThreadChannel.class);
        when(thread.getGuild()).thenReturn(guild);
        when(thread.getParentChannel()).thenReturn(parent);
        when(thread.getIdLong()).thenReturn(300L);
        when(thread.getOwnerIdLong()).thenReturn(777L);
        when(thread.getName()).thenReturn("sprint-planning");

        // when
        GuildThreadEventInfo info = JdaMapper.toThreadEventInfo(thread);

        // then
        assertThat(info.guildId()).isEqualTo(1L);
        assertThat(info.parentChannelId()).isEqualTo(50L);
        assertThat(info.threadId()).isEqualTo(300L);
        assertThat(info.ownerId()).isEqualTo(777L);
        assertThat(info.name()).isEqualTo("sprint-planning");
    }

    @Test
    @DisplayName("ThreadChannel의 ownerIdLong이 0이면 ownerId는 null로 정규화된다")
    void toThreadEventInfo_zeroOwnerId_ownerIdNormalizedToNull() {
        // given
        IThreadContainerUnion parent = mock(IThreadContainerUnion.class);
        when(parent.getIdLong()).thenReturn(50L);

        Guild guild = guildWithId(1L);
        ThreadChannel thread = mock(ThreadChannel.class);
        when(thread.getGuild()).thenReturn(guild);
        when(thread.getParentChannel()).thenReturn(parent);
        when(thread.getIdLong()).thenReturn(300L);
        when(thread.getOwnerIdLong()).thenReturn(0L);
        when(thread.getName()).thenReturn("anon");

        // when
        GuildThreadEventInfo info = JdaMapper.toThreadEventInfo(thread);

        // then
        assertThat(info.ownerId()).isNull();
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

    private static MessageChannelUnion threadChannel(long id, ChannelType parentType) {
        IThreadContainerUnion parent = mock(IThreadContainerUnion.class);
        doReturn(parentType).when(parent).getType();

        MessageChannelUnion channel = mock(MessageChannelUnion.class,
            withSettings().extraInterfaces(ThreadChannel.class));
        doReturn(id).when(channel).getIdLong();
        doReturn(parent).when((ThreadChannel) channel).getParentChannel();
        return channel;
    }

    private static MessageReactionAddEvent reactionAddEvent(
        User user, String emojiCode, long guildId, long channelId, long messageId, long userIdLong
    ) {
        EmojiUnion emoji = mock(EmojiUnion.class);
        doReturn(emojiCode).when(emoji).getAsReactionCode();

        MessageReaction reaction = mock(MessageReaction.class);
        doReturn(emoji).when(reaction).getEmoji();

        Guild guild = guildWithId(guildId);
        MessageChannelUnion channel = channelWithId(channelId);

        MessageReactionAddEvent event = mock(MessageReactionAddEvent.class);
        doReturn(guild).when(event).getGuild();
        doReturn(channel).when(event).getChannel();
        doReturn(messageId).when(event).getMessageIdLong();
        doReturn(userIdLong).when(event).getUserIdLong();
        doReturn(user).when(event).getUser();
        doReturn(reaction).when(event).getReaction();
        return event;
    }
}
