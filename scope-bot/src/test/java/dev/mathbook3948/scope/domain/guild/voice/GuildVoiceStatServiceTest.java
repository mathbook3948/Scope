package dev.mathbook3948.scope.domain.guild.voice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GuildVoiceStatServiceTest {

    @InjectMocks
    GuildVoiceStatService guildVoiceStatService;

    @Mock
    GuildVoiceStatRepository guildVoiceStatRepository;

    @Test
    @DisplayName("음성 채널 체류 통계를 정상적으로 저장한다")
    void createGuildVoiceStat_validInput_savesEntity() {
        // given
        GuildVoiceEventInfo info = new GuildVoiceEventInfo(1L, 100L, 200L, Instant.parse("2026-04-14T12:00:00Z"));
        long duration = 3600L;

        // when
        guildVoiceStatService.createGuildVoiceStat(info, duration);

        // then
        ArgumentCaptor<GuildVoiceStat> captor = ArgumentCaptor.forClass(GuildVoiceStat.class);
        verify(guildVoiceStatRepository).save(captor.capture());

        GuildVoiceStat saved = captor.getValue();
        assertThat(saved.getGuildId()).isEqualTo(1L);
        assertThat(saved.getChannelId()).isEqualTo(100L);
        assertThat(saved.getMemberId()).isEqualTo(200L);
        assertThat(saved.getDuration()).isEqualTo(3600L);
    }

    @Test
    @DisplayName("길드 ID로 통계를 삭제한다")
    void deleteAllByGuildId_validGuildId_delegatesToRepository() {
        // given
        Long guildId = 1L;

        // when
        guildVoiceStatService.deleteAllByGuildId(guildId);

        // then
        verify(guildVoiceStatRepository).deleteAllByGuildId(guildId);
    }

    @Test
    @DisplayName("지정 시점 이전의 통계를 삭제한다")
    void deleteAllBefore_validInstant_delegatesToRepository() {
        // given
        Instant before = Instant.parse("2026-04-01T00:00:00Z");

        // when
        guildVoiceStatService.deleteAllBefore(before);

        // then
        verify(guildVoiceStatRepository).deleteAllByCreatedAtBefore(before);
    }
}
