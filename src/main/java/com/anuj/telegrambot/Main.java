package com.anuj.telegrambot;

import com.anuj.telegrambot.bot.ShortageTrackerBot;
import com.anuj.telegrambot.contant.MySqlValues;
import com.anuj.telegrambot.utils.MySQLConnectionUtils;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        Connection connection = MySQLConnectionUtils.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(MySqlValues.CREATE_TABLE_IF_NOT_EXIST);
        preparedStatement.executeUpdate();
        try {
            telegramBotsApi.registerBot(new ShortageTrackerBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }

}
