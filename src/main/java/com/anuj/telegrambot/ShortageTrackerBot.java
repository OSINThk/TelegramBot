package com.anuj.telegrambot;

import com.anuj.telegrambot.contant.BotCommands;
import com.anuj.telegrambot.contant.BotTokens;
import com.anuj.telegrambot.handler.StartHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ShortageTrackerBot extends TelegramLongPollingBot {


    public void onUpdateReceived(Update update) {
        try {
            if(update.hasMessage() && update.getMessage().hasText()){
                if(update.getMessage().getText().equals(BotCommands.START_COMMAND)){
                    execute(StartHandler.startCollectingInformation(update));
                }
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public String getBotUsername() {
        return BotTokens.BOT_USER_NAME;
    }

    public String getBotToken() {
        return BotTokens.BOT_TOKEN;
    }
}
