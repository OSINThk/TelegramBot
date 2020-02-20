package com.anuj.telegrambot.handler;

import com.anuj.telegrambot.exception.UserNotFoundException;
import com.anuj.telegrambot.model.db.Report;
import com.anuj.telegrambot.model.db.User;
import com.anuj.telegrambot.repository.ReportRepository;
import com.anuj.telegrambot.repository.UserRepository;
import com.anuj.telegrambot.service.ProductService;
import com.anuj.telegrambot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class LocationHandler {

    private final UserService userService;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    @Autowired
    public LocationHandler(UserService userService,
                           ReportRepository reportRepository,
                           UserRepository userRepository,
                           ProductService productService){
        this.userService = userService;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.productService = productService;
    }

    public SendMessage setReportLocation(Update update){
        Integer telegramUserId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        try{
            User user = userService.getUserFromTelegramId(telegramUserId);
            Report report = new Report();
            report.setLongitude(update.getMessage().getLocation().getLongitude());
            report.setLatitude(update.getMessage().getLocation().getLatitude());
            report.setUser(user);
            report = reportRepository.save(report);
            user.getReportList().add(report);
            userRepository.save(user);
            SendMessage message = new SendMessage() // Create a message object object
                    .setChatId(update.getMessage().getChatId())
                    .setText("Please Choose One category");
            message.setReplyMarkup(fetchListOfProduct(report.getIdReport()));
            return message;

        }catch (UserNotFoundException e){
            return new SendMessage()
                    .setText("User not found \n" +
                            "Please  send message  \"/start\"")
                    .setChatId(chatId);
        }
    }

    private InlineKeyboardMarkup fetchListOfProduct(Long reportChatId) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        Iterator entries = productService.getProductListFromDatabase().entrySet().iterator();
        while (entries.hasNext()) {
            List<InlineKeyboardButton> column = new ArrayList<>();
            Map.Entry pair = (Map.Entry) entries.next();
            column.add(new InlineKeyboardButton().setText(pair.getValue().toString()).setCallbackData(pair.getKey().toString()+"_report_"+reportChatId));
            if (entries.hasNext()) {
                pair = (Map.Entry) entries.next();
                column.add(new InlineKeyboardButton().setText(pair.getValue().toString()).setCallbackData(pair.getKey().toString()+"_report_"+reportChatId));
            }
            rowsInline.add(column);


        }
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

}
