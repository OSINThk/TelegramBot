package com.anuj.telegrambot.handler;

import com.anuj.telegrambot.bot.ShortageTrackerBot;
import com.anuj.telegrambot.contant.MySqlValues;
import com.anuj.telegrambot.contant.PriceValueDetail;
import com.anuj.telegrambot.contant.RegexConstant;
import com.anuj.telegrambot.exception.ReportNotFoundException;
import com.anuj.telegrambot.exception.UserNotFoundException;
import com.anuj.telegrambot.model.db.Report;
import com.anuj.telegrambot.model.db.User;
import com.anuj.telegrambot.repository.ReportRepository;
import com.anuj.telegrambot.repository.UserRepository;
import com.anuj.telegrambot.service.ReportService;
import com.anuj.telegrambot.service.UserService;
import com.anuj.telegrambot.utils.MySQLConnectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PriceHandler {


    private final UserService userService;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final GeneralHandler generalHandler;
    private final ShortageTrackerBot shortageTrackerBot;
    private final ReportService reportService;

    @Autowired
    public PriceHandler(UserService userService,
                        ReportRepository reportRepository,
                        UserRepository userRepository,
                        GeneralHandler generalHandler,
                        @Lazy ShortageTrackerBot shortageTrackerBot,
                        ReportService reportService) {
        this.userService = userService;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.generalHandler = generalHandler;
        this.shortageTrackerBot = shortageTrackerBot;
        this.reportService = reportService;
    }

    public void setExpensiveness(Update update) {
        try {
            String callbackText = update.getCallbackQuery().getData();
            Integer telegramUserId = update.getCallbackQuery().getFrom().getId();
            Pattern pattern = Pattern.compile(RegexConstant.PRODUCT_EXPENSIVE_FORMAT);
            Matcher matcher = pattern.matcher(callbackText);
            String keyMap = "";
            String idReport = "";
            boolean matched = false;
            while (matcher.find()) {
                matched = true;
                keyMap = matcher.group(1);
                idReport = matcher.group(2);
            }
            if (matched) {
                try {
                    int expensiveLevel = Integer.parseInt(keyMap);
                    Long verifiedReportId = Long.valueOf(idReport);
                    User user = userService.getUserFromTelegramId(telegramUserId);
                    Report report = reportService.getReport(verifiedReportId, user);
                    report.setProductPrice(expensiveLevel);
                    report = reportRepository.save(report);
                    shortageTrackerBot.execute(generalHandler.decisionInlineKeyboardEdit(update));
                    String messageText =  "<b>Your Report:</b> \n" +
                            "\n===================================\n"+
                            "\nReport Id: <b>" + report.getIdReport() +"</b>"+
                            "\nLatitude: <b>"+report.getLatitude()+"</b>"+
                            "\nLongitude: <b>"+report.getLongitude()+"</b>"+
                            "\nProduct Name: <b>" + report.getLocaleName() +"</b>"+
                            "\nProduct Scarcity Level: <b>" + report.getProductScarcity() +"</b>"+
                            "\nProduct Expensive Level: <b>" + report.getProductPrice() +"</b>"+
                            "\n\n===================================\n" +
                            "\nPlease enter note by starting with /note report_id  For. eg.\n\"<b>/note "+report.getIdReport()+"  This is sample note </b>\" ";
                    shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update, messageText));
                } catch (UserNotFoundException e) {
                    shortageTrackerBot.execute(generalHandler.decisionInlineKeyboardEdit(update));
                    shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update, "Receiver do not have address created\n" +
                            "\"User not found \n" +
                            "Please  send message  \"/start\""));
                } catch (ReportNotFoundException e) {
                    e.printStackTrace();
                    e.printStackTrace();
                    shortageTrackerBot.execute(generalHandler.decisionInlineKeyboardEdit(update));
                    shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update, "Receiver do not have address created\n" +
                            "\"Report not found \n" +
                            "Please  send message  \"/start\""));
                }
            } else {
                shortageTrackerBot.execute(generalHandler.decisionInlineKeyboardEdit(update));
                shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update, "Something went wrong. Please try again later"));

            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
