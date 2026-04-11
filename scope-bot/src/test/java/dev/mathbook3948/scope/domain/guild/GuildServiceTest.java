package dev.mathbook3948.scope.domain.guild;

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
class GuildServiceTest {

    @InjectMocks
    GuildService guildService;

    @Mock
    GuildRepository guildRepository;

    @Test
    @DisplayName("기존 길드는 이름을 업데이트하고 신규 길드는 저장한다")
    void upsertGuilds_mixedExistingAndNew_updatesExistingAndSavesNew() {
        // given
        Guild existing = Guild.of(1L, "OldName");
        when(guildRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(existing));

        List<GuildInfo> guilds = List.of(
            new GuildInfo(1L, "NewName"),
            new GuildInfo(2L, "BrandNew")
        );

        // when
        guildService.upsertGuilds(guilds);

        // then
        assertThat(existing.getName()).isEqualTo("NewName");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Guild>> captor = ArgumentCaptor.forClass(List.class);
        verify(guildRepository).saveAll(captor.capture());

        List<Guild> saved = captor.getValue();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getGuildId()).isEqualTo(2L);
        assertThat(saved.get(0).getName()).isEqualTo("BrandNew");
    }

    @Test
    @DisplayName("모두 기존 길드이고 이름이 같으면 저장하지 않는다")
    void upsertGuilds_allExistingSameName_noSave() {
        // given
        Guild existing = Guild.of(1L, "SameName");
        when(guildRepository.findAllById(List.of(1L))).thenReturn(List.of(existing));

        // when
        guildService.upsertGuilds(List.of(new GuildInfo(1L, "SameName")));

        // then
        verify(guildRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("모두 신규 길드면 전부 저장한다")
    void upsertGuilds_allNew_savesAll() {
        // given
        when(guildRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of());

        List<GuildInfo> guilds = List.of(
            new GuildInfo(1L, "Guild1"),
            new GuildInfo(2L, "Guild2")
        );

        // when
        guildService.upsertGuilds(guilds);

        // then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Guild>> captor = ArgumentCaptor.forClass(List.class);
        verify(guildRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }
}
