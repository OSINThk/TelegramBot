package com.anuj.telegrambot.handler;

import com.anuj.telegrambot.contant.MySqlValues;
import com.anuj.telegrambot.contant.ProductDetail;
import com.anuj.telegrambot.utils.MySQLConnectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class LocationHandler {

    public static SendMessage setLocation(Update update) {
        Connection connection = MySQLConnectionUtils.getConnection();
        if (connection == null) {
            return new SendMessage().setText("Please Try Again Later");
        }
        Integer userID = update.getMessage().getFrom().getId();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(MySqlValues.UPDATE_LOCATION_STATEMENT);
            preparedStatement.setFloat(1, update.getMessage().getLocation().getLatitude());
            preparedStatement.setFloat(2, update.getMessage().getLocation().getLongitude());
            preparedStatement.setInt(3, userID);
            if (preparedStatement.executeUpdate() > 0) {
                SendMessage message = new SendMessage() // Create a message object object
                        .setChatId(update.getMessage().getChatId())
                        .setText("Please Choose One category");
                message.setReplyMarkup(fetchListOfProduct());
                return message;
            }
            System.out.println(preparedStatement.executeUpdate());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new SendMessage().setText("Please Try Again Later");

    }

    private static InlineKeyboardMarkup fetchListOfProduct() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        Iterator entries = ProductDetail.productMap.entrySet().iterator();
        while (entries.hasNext()) {
            List<InlineKeyboardButton> column = new ArrayList<>();
            Map.Entry pair = (Map.Entry) entries.next();
            column.add(new InlineKeyboardButton().setText(pair.getValue().toString()).setCallbackData(pair.getKey().toString()));
            if (entries.hasNext()) {
                pair = (Map.Entry) entries.next();
                column.add(new InlineKeyboardButton().setText(pair.getValue().toString()).setCallbackData(pair.getKey().toString()));
            }
            rowsInline.add(column);


        }
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

}
