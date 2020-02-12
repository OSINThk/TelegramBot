package com.anuj.telegrambot;

import com.anuj.telegrambot.utils.MySQLConnectionUtils;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        MySQLConnectionUtils utils = new MySQLConnectionUtils();
        utils.readDatabase();
        try{
            telegramBotsApi.registerBot(new ShortageTrackerBot());
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

}
