package dev.mathbook3948.scope.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "scope.discord")
@Validated
public record DiscordProperties(
        @NotBlank String token
) {}
