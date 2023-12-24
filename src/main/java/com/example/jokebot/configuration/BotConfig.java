package com.example.jokebot.configuration;

import org.springframework.beans.factory.annotation.Value;

public class BotConfig {
    @Value("$(bot.name)")
    String botName;
    @Value("$(bot.name)")
    String token;


}
