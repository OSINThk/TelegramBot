package com.anuj.telegrambot.handler;

import com.anuj.telegrambot.contant.LanguageType;
import com.anuj.telegrambot.model.db.User;
import com.anuj.telegrambot.model.dto.UserDto;
import com.anuj.telegrambot.repository.UserRepository;
import com.anuj.telegrambot.utils.LocaleUtils;
import com.vdurmont.emoji.EmojiParser;
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
public class StartHandler {

    private final UserRepository userRepository;
    private final LocaleUtils localeUtils;

    @Autowired
    public StartHandler(UserRepository userRepository,
                        LocaleUtils localeUtils){
        this.userRepository = userRepository;
        this.localeUtils = localeUtils;
    }


    public SendMessage startChat(Update update){
        Integer telegramUserId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        Optional<User> userOptional = userRepository.findByTelegramUserId(telegramUserId);
        User user = null;
        if(userOptional.isPresent()){
            user= userOptional.get();
        }else{
            UserDto userDto = new UserDto();
            userDto.setTelegramUserId(telegramUserId);
            userDto.setTelegramUserName(update.getMessage().getFrom().getUserName());
            userDto.setLanguageType(LanguageType.en);
            user = User.getUserDto(userDto);

        }
        ResourceBundle resourceBundle = localeUtils.getMessageResource(LanguageType.en);
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(resourceBundle.getString("start.welcome")+ "@"+user.getTelegramUserName());
        message.setReplyMarkup(getBasicReplyKeyboardMarkup());
        message.setReplyToMessageId(update.getMessage().getMessageId());
        return message;


    }

    public ReplyKeyboardMarkup getBasicReplyKeyboardMarkup(){
        // Create ReplyKeyboardMarkup object
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<>();
        // Create a keyboard row
        KeyboardRow row = new KeyboardRow();
        // Set each button, you can also use KeyboardButton objects if you need something else than text
        row.add(EmojiParser.parseToUnicode("Language"));
        KeyboardButton keyboardButton =new KeyboardButton("New Report");
        keyboardButton.setRequestLocation(true);
        row.add(keyboardButton);

        // Add the first row to the keyboard
        keyboard.add(row);


        // Set the keyboard to the markup
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        // Add it to the message
        return keyboardMarkup;
    }

}


