package com.anuj.telegrambot.handler;

import com.anuj.telegrambot.contant.MySqlValues;
import com.anuj.telegrambot.model.db.Report;
import com.anuj.telegrambot.utils.MySQLConnectionUtils;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StartHandler {

    public static SendMessage startCollectingInformation(Update update) {
        Connection connection = MySQLConnectionUtils.getConnection();
        if (connection == null) {
            return new SendMessage().setText("Please Try Again Later");
        }
        Integer userId = update.getMessage().getFrom().getId();
        try {
            PreparedStatement getDataPreparedStatement = connection.prepareStatement(MySqlValues.GET_DATA_STATEMENT);
            getDataPreparedStatement.setInt(1, userId);
            ResultSet set = getDataPreparedStatement.executeQuery();
            int count = 0;
            while (set.next()) {
                count++;
            }
            if(count!=0){
                System.out.println("Data is already present. Deleting current data");
                PreparedStatement preparedStatement = connection.prepareStatement(MySqlValues.DELETE_DATA_STATEMENT);
                preparedStatement.setInt(1, userId);
                preparedStatement.executeUpdate();
            }
            System.out.println(insertInitialData(connection,userId));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        SendMessage sendMessage = new SendMessage(update.getMessage().getChatId(), "Please allow us to populate your location")
                .setParseMode(ParseMode.HTML);
        KeyboardButton keyboardButton = new KeyboardButton("Give Location Access");
        keyboardButton.setRequestLocation(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(keyboardButton);
        keyboardRows.add(keyboardRow);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);
        replyKeyboardMarkup.setOneTimeKeyboard(true)
                .setResizeKeyboard(true)
                .setSelective(true);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;

    }

    public static int insertInitialData(Connection connection, Integer userId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(MySqlValues.REPORT_INSERT_STATEMENT);
        preparedStatement.setInt(1, userId);
        return preparedStatement.executeUpdate();
    }
}


