package com.anuj.telegrambot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;

@Configuration
public class AppConfig {

    @Bean
    TelegramBotsApi telegramBotsApi(){
        return new TelegramBotsApi();
    }


}
