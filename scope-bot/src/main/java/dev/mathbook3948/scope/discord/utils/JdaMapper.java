package dev.mathbook3948.scope.discord.utils;

import dev.mathbook3948.scope.domain.guild.AuthorType;
import dev.mathbook3948.scope.domain.guild.channel.GuildChannelInfo;
import dev.mathbook3948.scope.domain.guild.channel.GuildChannelType;
import dev.mathbook3948.scope.domain.guild.message.GuildMessageEventInfo;
import dev.mathbook3948.scope.domain.guild.message.GuildMessageSourceType;
import dev.mathbook3948.scope.domain.guild.reaction.GuildReactionEventInfo;
import dev.mathbook3948.scope.domain.guild.thread.GuildThreadEventInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IPositionableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;

/**
 * JDA 타입을 도메인 타입으로 변환하는 유틸 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JdaMapper {

    /**
     * JDA Channel을 {@link GuildChannelInfo}로 변환한다.
     * <p>채널 타입에 따라 지원하는 속성이 다르므로 instanceof로 분기한다.
     * <ul>
     *   <li>{@link ICategorizableChannel} — TEXT, VOICE, FORUM 등 카테고리 하위 채널. CATEGORY 자체는 미구현</li>
     *   <li>{@link IPositionableChannel} — 정렬 가능한 채널. 스레드 등은 미구현</li>
     * </ul>
     * 미지원 타입은 해당 필드가 {@code null}로 저장된다.
     */
    public static GuildChannelInfo toChannelInfo(Channel channel) {
        Long parentId = null;
        if (channel instanceof ICategorizableChannel categorizable && categorizable.getParentCategory() != null) {
            parentId = categorizable.getParentCategory().getIdLong();
        }
        Integer position = null;
        if (channel instanceof IPositionableChannel positionable) {
            position = positionable.getPosition();
        }
        return new GuildChannelInfo(
            channel.getIdLong(),
            channel.getName(),
            GuildChannelType.from(channel.getType().name()),
            parentId,
            position
        );
    }

    public static GuildReactionEventInfo toReactionEventInfo(GenericMessageReactionEvent event) {
        User user = event.getUser();
        AuthorType authorType = user == null
            ? AuthorType.UNKNOWN
            : AuthorType.from(user.isBot(), user.isSystem());
        return new GuildReactionEventInfo(
            event.getGuild().getIdLong(),
            event.getChannel().getIdLong(),
            event.getMessageIdLong(),
            event.getUserIdLong(),
            event.getReaction().getEmoji().getAsReactionCode(),
            authorType
        );
    }

    public static GuildMessageEventInfo toMessageEventInfo(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();
        User author = event.getAuthor();
        return new GuildMessageEventInfo(
            event.getGuild().getIdLong(),
            event.getChannel().getIdLong(),
            author.getIdLong(),
            event.getMessageIdLong(),
            content.codePointCount(0, content.length()),
            sourceTypeOf(event.getChannel()),
            AuthorType.from(author.isBot(), author.isSystem())
        );
    }

    public static GuildMessageEventInfo toMessageEventInfo(MessageUpdateEvent event) {
        String content = event.getMessage().getContentRaw();
        User author = event.getAuthor();
        return new GuildMessageEventInfo(
            event.getGuild().getIdLong(),
            event.getChannel().getIdLong(),
            author.getIdLong(),
            event.getMessageIdLong(),
            content.codePointCount(0, content.length()),
            sourceTypeOf(event.getChannel()),
            AuthorType.from(author.isBot(), author.isSystem())
        );
    }

    public static GuildMessageEventInfo toMessageDeleteInfo(MessageDeleteEvent event) {
        return new GuildMessageEventInfo(
            event.getGuild().getIdLong(),
            event.getChannel().getIdLong(),
            null,
            event.getMessageIdLong(),
            null,
            sourceTypeOf(event.getChannel()),
            AuthorType.UNKNOWN
        );
    }

    /**
     * 메시지 채널의 출처 타입을 판별한다.
     * <p>포럼 포스트도 JDA에서는 {@link ThreadChannel}이므로, 부모가 {@link ChannelType#FORUM}인지
     * 확인해 {@code FORUM}과 일반 {@code THREAD}를 구분한다. 미디어 채널 포스트는 현재 {@code THREAD}로 분류된다.
     */
    private static GuildMessageSourceType sourceTypeOf(MessageChannel channel) {
        if (channel instanceof ThreadChannel thread) {
            return thread.getParentChannel().getType() == ChannelType.FORUM
                ? GuildMessageSourceType.FORUM
                : GuildMessageSourceType.THREAD;
        }

        //TODO MEDIA도 처리필요

        return GuildMessageSourceType.CHANNEL;
    }

    public static GuildThreadEventInfo toThreadEventInfo(ThreadChannel thread) {
        long ownerId = thread.getOwnerIdLong();
        return new GuildThreadEventInfo(
            thread.getGuild().getIdLong(),
            thread.getParentChannel().getIdLong(),
            thread.getIdLong(),
            ownerId == 0L ? null : ownerId,
            thread.getName()
        );
    }
}
