package dev.mathbook3948.scope.discord.utils;

import dev.mathbook3948.scope.domain.guild.channel.GuildChannelInfo;
import dev.mathbook3948.scope.domain.guild.channel.GuildChannelType;
import dev.mathbook3948.scope.domain.guild.reaction.GuildReactionEventInfo;
import dev.mathbook3948.scope.domain.guild.thread.GuildThreadEventInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IPositionableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
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
        return new GuildReactionEventInfo(
            event.getGuild().getIdLong(),
            event.getChannel().getIdLong(),
            event.getMessageIdLong(),
            event.getUserIdLong(),
            event.getReaction().getEmoji().getAsReactionCode()
        );
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
