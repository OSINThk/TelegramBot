package com.anuj.telegrambot.handler;

import com.anuj.telegrambot.contant.MySqlValues;
import com.anuj.telegrambot.contant.PriceValueDetail;
import com.anuj.telegrambot.contant.ScarcityValueDetail;
import com.anuj.telegrambot.model.db.Report;
import com.anuj.telegrambot.utils.MySQLConnectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PriceHandler {

    public static SendMessage updateExpensiveness(Update update) {
        Connection connection = MySQLConnectionUtils.getConnection();
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String valueKey = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer expensivenessValue = PriceValueDetail.expensiveValueMap.get(valueKey);
        Integer userId = callbackQuery.getFrom().getId();
        if (expensivenessValue != null) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(MySqlValues.UPDATE_EXPENSIVE_VALUE_STATEMENT);
                preparedStatement.setInt(1, expensivenessValue);
                preparedStatement.setInt(2, userId);
                if (preparedStatement.executeUpdate() > 0) {
                    PreparedStatement getDataPreparedStatement = connection.prepareStatement(MySqlValues.GET_DATA_STATEMENT);
                    getDataPreparedStatement.setInt(1, userId);
                    ResultSet set = getDataPreparedStatement.executeQuery();
                    Report report = new Report();
                    while (set.next()) {
                        report.setLatitude(set.getDouble("latitude"));
                        report.setLongitude(set.getDouble("longitude"));
                        report.setProductName(set.getString("product_name"));
                        report.setProductScarcity(set.getInt("product_scarcity"));
                        report.setProductPrice(set.getInt("product_price"));
                    }

                    if (report.getProductName() != null) {
                        SendMessage sendMessage = new SendMessage()
                                .setText("Your report is as follow: " + "\n" +
                                        "Latitude: <b>" + report.getLatitude() + "</b>\n" +
                                        "Longitude: <b>" + report.getLongitude() + "</b>\n" +
                                        "Product Name: <b>" + report.getProductName() + "</b>\n" +
                                        "Product Scarcity: <b>" + report.getProductScarcity() + "</b>\n" +
                                        "Product Price: <b>" + report.getProductPrice() + "</b>\n" +
                                        "<i>Do you want to submit this report ?</i>");
                        sendMessage.enableHtml(true);
                        sendMessage.setReplyMarkup(designConfirmationKeyboard());
                        sendMessage.setChatId(chatId);
                        return sendMessage;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }
        return new SendMessage().setText("Please Try Again Later");

    }

    public static InlineKeyboardMarkup designConfirmationKeyboard() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> column = new ArrayList<>();
        column.add(new InlineKeyboardButton().setText("Submit").setCallbackData("submit_true"));
        column.add(new InlineKeyboardButton().setText("Cancel").setCallbackData("submit_false"));
        rowsInline.add(column);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

}
