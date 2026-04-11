package dev.mathbook3948.scope.properties;

import java.time.Duration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "scope.job")
@Validated
public record JobProperties(
        @Valid GuildMemberStatJob guildMemberStat,
        @Valid GuildMemberEventCleanupJob guildMemberEventCleanup,
        @Valid GuildMemberStatCleanupJob guildMemberStatCleanup,
        @Valid GuildMessageEventCleanupJob guildMessageEventCleanup
) {
    public record GuildMemberStatJob(@NotBlank String cron) {}
    public record GuildMemberEventCleanupJob(@NotBlank String cron, @NotNull Duration retention) {}
    public record GuildMemberStatCleanupJob(@NotBlank String cron, @NotNull Duration retention) {}
    public record GuildMessageEventCleanupJob(@NotBlank String cron, @NotNull Duration retention) {}
}
