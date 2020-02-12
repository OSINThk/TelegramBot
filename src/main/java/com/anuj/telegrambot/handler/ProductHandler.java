package com.anuj.telegrambot.handler;

import com.anuj.telegrambot.contant.MySqlValues;
import com.anuj.telegrambot.contant.ProductDetail;
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

public class ProductHandler {

    public static SendMessage updateProduct(Update update) {
        Connection connection = MySQLConnectionUtils.getConnection();
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String productKey = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        String productName = ProductDetail.productMap.get(productKey);
        Integer userId = callbackQuery.getFrom().getId();
        if (productName != null) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(MySqlValues.UPDATE_PRODUCT_NAME_STATEMENT);
                preparedStatement.setString(1, productName);
                preparedStatement.setInt(2, userId);
                if (preparedStatement.executeUpdate() > 0) {
                    SendMessage sendMessage = new SendMessage()
                            .setText("You have selected \"<b>"+ productName+"</b>\"\n" +
                                    "Please Select the scarcity level\n" +
                                    "" +
                                    "5: High\n" +
                                    "1: Normal\n")
                            .setChatId(chatId);
                    sendMessage.setReplyMarkup(designScarcityKeyboard());
                    sendMessage.enableHtml(true);
                    return sendMessage;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new SendMessage().setText("Please Try Again Later");
    }

    private static InlineKeyboardMarkup designScarcityKeyboard(){
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        Iterator entries = ScarcityValueDetail.scarcityValueMap.entrySet().iterator();
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
