package com.anuj.telegrambot.handler;

import com.anuj.telegrambot.bot.ShortageTrackerBot;
import com.anuj.telegrambot.contant.LanguageType;
import com.anuj.telegrambot.contant.RegexConstant;
import com.anuj.telegrambot.exception.UserNotFoundException;
import com.anuj.telegrambot.model.db.User;
import com.anuj.telegrambot.repository.UserRepository;
import com.anuj.telegrambot.service.UserService;
import com.anuj.telegrambot.utils.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LanguageHandler {

    private final LocaleUtils localeUtils;
    private final ShortageTrackerBot shortageTrackerBot;
    private final GeneralHandler generalHandler;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public LanguageHandler(LocaleUtils localeUtils,
                           @Lazy ShortageTrackerBot shortageTrackerBot,
                           GeneralHandler generalHandler,
                           UserService userService,
                           UserRepository userRepository) {
        this.localeUtils = localeUtils;
        this.shortageTrackerBot = shortageTrackerBot;
        this.generalHandler = generalHandler;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public SendMessage getLanguageList(Update update){
        Integer telegramUserId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please choose language");
        sendMessage.setChatId(chatId);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton().setText("English").setCallbackData("language_option_1"));
        rowInline.add(new InlineKeyboardButton().setText("简体").setCallbackData("language_option_2"));
        rowInline.add(new InlineKeyboardButton().setText("繁体").setCallbackData("language_option_3"));
        rowInline.add(new InlineKeyboardButton().setText("繁体(台灣)").setCallbackData("language_option_4"));
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);
        sendMessage.enableHtml(true);
        sendMessage.enableWebPagePreview();
        return sendMessage;
    }

    public void chooseLanguage(Update update){
        try{
            String callbackText = update.getCallbackQuery().getData();
            Integer telegramUserId = update.getCallbackQuery().getFrom().getId();
            Pattern pattern = Pattern.compile(RegexConstant.LANGUAGE_CHOOSE_OPTION);
            Matcher matcher = pattern.matcher(callbackText);
            String idLanguage = "";
            boolean matched = false;
            while (matcher.find()) {
                matched = true;
                idLanguage= matcher.group(1);
            }
            if(matched){
                LanguageType languageType;
                if(idLanguage.equals("2")){
                    languageType = LanguageType.zh_CN;
                }else if(idLanguage.equals("3")){
                    languageType = LanguageType.zh_HK;
                }else if(idLanguage.equals("4")){
                    languageType = LanguageType.zh_TW;
                }else {
                    languageType= LanguageType.en;
                }
                ResourceBundle resourceBundle = localeUtils.getMessageResource(languageType);
                try{
                    User user = userService.getUserFromTelegramId(telegramUserId);
                    user.setLanguageType(languageType);
                    user = userRepository.save(user);
                    shortageTrackerBot.execute(generalHandler.decisionInlineKeyboardEdit(update));
                    shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update,new String(resourceBundle.getString("language.change-success").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)));
                }catch (UserNotFoundException e){
                    shortageTrackerBot.execute(generalHandler.decisionInlineKeyboardEdit(update));
                    shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update,new String(resourceBundle.getString("user.not-found").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)));

                }
            }else {
                shortageTrackerBot.execute(generalHandler.decisionInlineKeyboardEdit(update));
                shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update, "Something went wrong. Please try again later"));

            }
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
