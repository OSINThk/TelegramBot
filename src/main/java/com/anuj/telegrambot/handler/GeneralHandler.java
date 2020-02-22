package com.anuj.telegrambot.handler;

import com.anuj.telegrambot.contant.ScarcityValueDetail;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

@Service
public class GeneralHandler {

    public EditMessageReplyMarkup decisionInlineKeyboardEdit(Update update) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setReplyMarkup(null);
        editMessageReplyMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageReplyMarkup.setChatId(update.getCallbackQuery().getMessage().getChatId());
        return editMessageReplyMarkup;
    }

    public EditMessageText decisionMessageEdit(Update update, String text) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setText(text);
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageText.setChatId(update.getCallbackQuery().getMessage().getChatId());
        editMessageText.enableHtml(true);
        return editMessageText;
    }

    public EditMessageReplyMarkup addSendOrCancel(ResourceBundle resourceBundle, Update update, Long idReport) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> column = new ArrayList<>();
        column.add(new InlineKeyboardButton().setText(resourceBundle.getString("send.yes")).setCallbackData("send_report_true_" + idReport));
        column.add(new InlineKeyboardButton().setText(resourceBundle.getString("send.no")).setCallbackData("send_report_false_" + idReport));
        rowsInline.add(column);
        markupInline.setKeyboard(rowsInline);
        editMessageReplyMarkup.setReplyMarkup(markupInline);
        editMessageReplyMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageReplyMarkup.setChatId(update.getCallbackQuery().getMessage().getChatId());
        return editMessageReplyMarkup;

    }

    public EditMessageReplyMarkup decisionMessageEditAddSkip(ResourceBundle resourceBundle, Update update, Long idReport) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> column = new ArrayList<>();
        column.add(new InlineKeyboardButton().setText(resourceBundle.getString("note.skip")).setCallbackData("skip_note_" + idReport));
        rowsInline.add(column);
        markupInline.setKeyboard(rowsInline);
        editMessageReplyMarkup.setReplyMarkup(markupInline);
        editMessageReplyMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageReplyMarkup.setChatId(update.getCallbackQuery().getMessage().getChatId());
        return editMessageReplyMarkup;

    }


    public EditMessageReplyMarkup levelInlineKeyboardEdit(Update update, HashMap<String, Integer> hashMap, Long idReport) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        Iterator entries = hashMap.entrySet().iterator();
        List<InlineKeyboardButton> column = new ArrayList<>();
        while (entries.hasNext()) {
            Map.Entry pair = (Map.Entry) entries.next();
            column.add(new InlineKeyboardButton().setText(pair.getValue().toString()).setCallbackData(pair.getKey().toString() + "_" + idReport));
        }
        rowsInline.add(column);
        markupInline.setKeyboard(rowsInline);
        editMessageReplyMarkup.setReplyMarkup(markupInline);
        editMessageReplyMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageReplyMarkup.setChatId(update.getCallbackQuery().getMessage().getChatId());
        return editMessageReplyMarkup;
    }
}
