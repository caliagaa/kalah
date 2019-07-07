package com.backbase.assignment.kalahgame.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
@EnableConfigurationProperties
@Getter
@Setter
public class GameConfiguration {

    @Value("${game.numberOfPits}")
    private int numberOfPits;

    @Value("${game.numberOfStones}")
    private int numberOfStones;
}
