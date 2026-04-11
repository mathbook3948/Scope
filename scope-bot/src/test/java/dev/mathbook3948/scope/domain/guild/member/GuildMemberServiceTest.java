package dev.mathbook3948.scope.domain.guild.member;

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
class GuildMemberServiceTest {

    @InjectMocks
    GuildMemberService guildMemberService;

    @Mock
    GuildMemberRepository guildMemberRepository;

    @Mock
    GuildRepository guildRepository;

    @Test
    @DisplayName("기존 멤버는 변경된 필드를 업데이트하고 신규 멤버는 저장한다")
    void upsertGuildMembers_mixedExistingAndNew_updatesExistingAndSavesNew() {
        // given
        Long guildId = 1L;
        Guild guildRef = Guild.of(guildId, "TestGuild");
        GuildMember existing = GuildMember.of(guildRef, 101L, "OldName", "old.png");

        when(guildMemberRepository.findByGuild_GuildIdAndMemberIdIn(guildId, List.of(101L, 102L)))
            .thenReturn(List.of(existing));
        when(guildRepository.getReferenceById(guildId)).thenReturn(guildRef);

        List<GuildMemberInfo> members = List.of(
            new GuildMemberInfo(101L, "NewName", "new.png"),
            new GuildMemberInfo(102L, "BrandNew", "brand.png")
        );

        // when
        guildMemberService.upsertGuildMembers(guildId, members);

        // then
        assertThat(existing.getName()).isEqualTo("NewName");
        assertThat(existing.getAvatarUrl()).isEqualTo("new.png");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<GuildMember>> captor = ArgumentCaptor.forClass(List.class);
        verify(guildMemberRepository).saveAll(captor.capture());

        List<GuildMember> saved = captor.getValue();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getMemberId()).isEqualTo(102L);
        assertThat(saved.get(0).getName()).isEqualTo("BrandNew");
    }

    @Test
    @DisplayName("모두 기존 멤버이고 값이 같으면 저장하지 않는다")
    void upsertGuildMembers_allExistingSameValues_noSave() {
        // given
        Long guildId = 1L;
        Guild guildRef = Guild.of(guildId, "TestGuild");
        GuildMember existing = GuildMember.of(guildRef, 101L, "Name", "avatar.png");

        when(guildMemberRepository.findByGuild_GuildIdAndMemberIdIn(guildId, List.of(101L)))
            .thenReturn(List.of(existing));

        // when
        guildMemberService.upsertGuildMembers(guildId, List.of(new GuildMemberInfo(101L, "Name", "avatar.png")));

        // then
        verify(guildMemberRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("모두 신규 멤버면 전부 저장한다")
    void upsertGuildMembers_allNew_savesAll() {
        // given
        Long guildId = 1L;
        Guild guildRef = Guild.of(guildId, "TestGuild");

        when(guildMemberRepository.findByGuild_GuildIdAndMemberIdIn(guildId, List.of(101L, 102L)))
            .thenReturn(List.of());
        when(guildRepository.getReferenceById(guildId)).thenReturn(guildRef);

        List<GuildMemberInfo> members = List.of(
            new GuildMemberInfo(101L, "Member1", "a1.png"),
            new GuildMemberInfo(102L, "Member2", "a2.png")
        );

        // when
        guildMemberService.upsertGuildMembers(guildId, members);

        // then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<GuildMember>> captor = ArgumentCaptor.forClass(List.class);
        verify(guildMemberRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }

}
