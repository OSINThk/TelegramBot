package com.anuj.telegrambot.bot;

import com.anuj.telegrambot.contant.BotCommands;
import com.anuj.telegrambot.contant.BotTokens;
import com.anuj.telegrambot.handler.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ShortageTrackerBot extends TelegramLongPollingBot {


    public void onUpdateReceived(Update update) {
        try {
            if(update.hasCallbackQuery()){
                if(update.getCallbackQuery().getData().startsWith("prod")){
                    execute(ProductHandler.updateProduct(update));
                }else if(update.getCallbackQuery().getData().startsWith("scarcity")){
                    execute(ScarcityHandler.updateValue(update));
                }else if (update.getCallbackQuery().getData().startsWith("expensive")){
                    execute(PriceHandler.updateExpensiveness(update));
                }else if(update.getCallbackQuery().getData().startsWith("submit")){
                    execute(SubmissionHandler.checkSubmissionDecision(update));
                }
            }
            if(update.hasMessage()&& update.getMessage().getLocation()!=null){
                execute(LocationHandler.setLocation(update));
            }
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
