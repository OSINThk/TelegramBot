package com.anuj.telegrambot.handler;

import com.anuj.telegrambot.contant.LanguageType;
import com.anuj.telegrambot.model.db.Report;
import com.anuj.telegrambot.model.db.User;
import com.anuj.telegrambot.model.dto.UserDto;
import com.anuj.telegrambot.repository.UserRepository;
import com.anuj.telegrambot.utils.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Service
public class ReportHandler {

    private final UserRepository userRepository;
    private final LocaleUtils localeUtils;

    @Autowired
    public ReportHandler(UserRepository userRepository,
                        LocaleUtils localeUtils) {
        this.userRepository = userRepository;
        this.localeUtils = localeUtils;

    }

    public SendMessage startInformationCollection(Update update) {
        Integer telegramUserId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        Optional<User> userOptional = userRepository.findByTelegramUserId(telegramUserId);
        User user = null;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.setLanguageType(LanguageType.zh_CN);//todo remove this line
            user = userRepository.save(user);//todo remove this line
        } else {
            UserDto userDto = new UserDto();
            userDto.setTelegramUserId(telegramUserId);
            userDto.setTelegramUserName(update.getMessage().getFrom().getUserName());
            String languageType = update.getMessage().getFrom().getLanguageCode();
            if (languageType.equals("zh-CN")) {
                userDto.setLanguageType(LanguageType.zh_CN);
            } else if (languageType.equals("zh-HK")) {
                userDto.setLanguageType(LanguageType.zh_HK);
            } else if (languageType.equals("zh-TW")) {
                userDto.setLanguageType(LanguageType.zh_TW);
            }else{
                userDto.setLanguageType(LanguageType.en);
            }
            userDto.setLanguageType(LanguageType.zh_CN); //todo remove this line
            user = User.getUserDto(userDto);
            Report report = new Report();
            user = userRepository.save(user);
        }
        ResourceBundle resourceBundle = localeUtils.getMessageResource(user.getLanguageType());
        KeyboardButton keyboardButton = new KeyboardButton(resourceBundle.getString("start.ask-location"));
        keyboardButton.setRequestLocation(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(keyboardButton);
        keyboardRows.add(keyboardRow);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);
        replyKeyboardMarkup.setOneTimeKeyboard(true)
                .setResizeKeyboard(true)
                .setSelective(true);
        return new SendMessage()
                .setChatId(chatId)
                .setText(resourceBundle.getString("start.welcome")+" <b>@" + user.getTelegramUserName() +
                        "</b>\n" +
                        resourceBundle.getString("start.ask-location-message"))
                .setReplyMarkup(replyKeyboardMarkup)
                .enableHtml(true);

    }

}
