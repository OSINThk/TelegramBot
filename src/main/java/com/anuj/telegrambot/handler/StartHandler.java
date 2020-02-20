package com.anuj.telegrambot.handler;

import com.anuj.telegrambot.contant.LanguageType;
import com.anuj.telegrambot.model.db.Report;
import com.anuj.telegrambot.model.db.User;
import com.anuj.telegrambot.model.dto.UserDto;
import com.anuj.telegrambot.repository.UserRepository;
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

@Service
public class StartHandler {

    private final UserRepository userRepository;

    @Autowired
    public StartHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public SendMessage startInformationCollection(Update update) {
        Integer telegramUserId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        Optional<User> userOptional = userRepository.findByTelegramUserId(telegramUserId);
        User user = null;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            UserDto userDto = new UserDto();
            userDto.setTelegramUserId(telegramUserId);
            userDto.setTelegramUserName(update.getMessage().getFrom().getUserName());
            String languageType = update.getMessage().getFrom().getLanguageCode();
            if (languageType.equals("en")) {
                userDto.setLanguageType(LanguageType.English);
            } else if (languageType.equals("hn-CN")) {
                userDto.setLanguageType(LanguageType.简体);
            } else if (languageType.equals("zh-HK")) {
                userDto.setLanguageType(LanguageType.繁体);
            } else if (languageType.equals("zh-TW")) {
                userDto.setLanguageType(LanguageType.繁体_台灣);
            }
            user = User.getUserDto(userDto);
            Report report = new Report();
            user = userRepository.save(user);
        }


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
        return new SendMessage()
                .setChatId(chatId)
                .setText("Welcome <b>@" + user.getTelegramUserName() +
                        "</b>\n" +
                        "Please allow us to populate your location.")
                .setReplyMarkup(replyKeyboardMarkup)
                .enableHtml(true);

    }

}


