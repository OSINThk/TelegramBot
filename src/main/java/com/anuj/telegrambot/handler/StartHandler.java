package com.anuj.telegrambot.handler;

import org.checkerframework.checker.units.qual.K;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class StartHandler {

    public static SendMessage startCollectingInformation(Update update) {
        SendMessage sendMessage = new SendMessage(update.getMessage().getChatId(), "message with keyboard")
                .setParseMode(ParseMode.HTML);
        KeyboardButton keyboardButton = new KeyboardButton("Location");
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
}


