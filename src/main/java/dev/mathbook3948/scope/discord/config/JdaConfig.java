package dev.mathbook3948.scope.discord.config;

import dev.mathbook3948.scope.properties.DiscordProperties;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class JdaConfig {

    @Bean
    public JDA jda(DiscordProperties properties, List<ListenerAdapter> listeners) throws InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(properties.token())
                .enableIntents(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_VOICE_STATES
                );
        listeners.forEach(builder::addEventListeners);
        return builder.build().awaitReady();
    }
}
