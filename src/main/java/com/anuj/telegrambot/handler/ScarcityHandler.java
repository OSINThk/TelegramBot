package com.anuj.telegrambot.handler;

import com.anuj.telegrambot.contant.PriceValueDetail;
import com.anuj.telegrambot.contant.MySqlValues;
import com.anuj.telegrambot.contant.ScarcityValueDetail;
import com.anuj.telegrambot.utils.MySQLConnectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ScarcityHandler {

    public static SendMessage updateValue(Update update) {
        Connection connection = MySQLConnectionUtils.getConnection();
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String valueKey = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer scarcityValue = ScarcityValueDetail.scarcityValueMap.get(valueKey);
        Integer userId = callbackQuery.getFrom().getId();
        if (scarcityValue != null) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(MySqlValues.UPDATE_SCARCITY_VALUE_STATEMENT);
                preparedStatement.setInt(1, scarcityValue);
                preparedStatement.setInt(2, userId);
                if (preparedStatement.executeUpdate() > 0) {
                    SendMessage sendMessage = new SendMessage()
                            .setText("Now Please Select Expensive level\n" +
                                    "5: High\n" +
                                    "1: Normal\n")
                            .setChatId(chatId);
                    sendMessage.setReplyMarkup(designExpensiveKeyboard());
                    return sendMessage;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new SendMessage().setText("Please Try Again Later");
    }

    private static InlineKeyboardMarkup designExpensiveKeyboard(){
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        Iterator entries = PriceValueDetail.expensiveValueMap.entrySet().iterator();
        List<InlineKeyboardButton> column = new ArrayList<>();
        while (entries.hasNext()) {
            Map.Entry pair = (Map.Entry) entries.next();
            column.add(new InlineKeyboardButton().setText(pair.getValue().toString()).setCallbackData(pair.getKey().toString()));
        }
        rowsInline.add(column);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

}
