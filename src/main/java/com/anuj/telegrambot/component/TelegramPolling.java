package com.anuj.telegrambot.component;

import com.anuj.telegrambot.bot.ShortageTrackerBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class TelegramPolling {

    private final TelegramBotsApi telegramBotsApi;
    private final ShortageTrackerBot shortageTrackerBot;

    @Autowired
    public TelegramPolling(TelegramBotsApi telegramBotsApi,
                           ShortageTrackerBot shortageTrackerBot){
        this.telegramBotsApi = telegramBotsApi;
        this.shortageTrackerBot = shortageTrackerBot;
    }

    @PostConstruct
    public void init(){
        try{
            telegramBotsApi.registerBot(shortageTrackerBot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

}
