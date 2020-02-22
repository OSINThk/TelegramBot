package com.anuj.telegrambot.handler;

import com.anuj.telegrambot.bot.ShortageTrackerBot;
import com.anuj.telegrambot.contant.PriceValueDetail;
import com.anuj.telegrambot.contant.RegexConstant;
import com.anuj.telegrambot.exception.ReportNotFoundException;
import com.anuj.telegrambot.exception.UserNotFoundException;
import com.anuj.telegrambot.model.db.Report;
import com.anuj.telegrambot.model.db.User;
import com.anuj.telegrambot.repository.ReportRepository;
import com.anuj.telegrambot.service.ReportService;
import com.anuj.telegrambot.service.UserService;
import com.anuj.telegrambot.utils.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScarcityHandler {

    private final UserService userService;
    private final ReportService reportService;
    private final ShortageTrackerBot shortageTrackerBot;
    private final GeneralHandler generalHandler;
    private final ReportRepository reportRepository;
    private final LocaleUtils localeUtils;

    @Autowired
    public ScarcityHandler(UserService userService,
                           ReportService reportService,
                           @Lazy ShortageTrackerBot shortageTrackerBot,
                           GeneralHandler generalHandler,
                           ReportRepository reportRepository,
                           LocaleUtils localeUtils) {
        this.userService = userService;
        this.reportService = reportService;
        this.shortageTrackerBot = shortageTrackerBot;
        this.generalHandler = generalHandler;
        this.reportRepository = reportRepository;
        this.localeUtils = localeUtils;
    }

    public void setScarcity(Update update) {
        try {
            String callbackText = update.getCallbackQuery().getData();
            Integer telegramUserId = update.getCallbackQuery().getFrom().getId();
            Pattern pattern = Pattern.compile(RegexConstant.PRODUCT_SCARCITY_FORMAT);
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
                    int verifiedScarcityLevel = Integer.parseInt(keyMap);
                    Long verifiedReportId = Long.valueOf(idReport);
                    User user = userService.getUserFromTelegramId(telegramUserId);
                    Report report = reportService.getReport(verifiedReportId, user);
                    report.setProductScarcity(verifiedScarcityLevel);
                    report = reportRepository.save(report);
                    ResourceBundle resourceBundle = localeUtils.getMessageResource(user.getLanguageType());
                            String messageText = "<b>"+resourceBundle.getString("report.your-report")+"</b> \n" +
                            "\n===================================\n" +
                            "\n"+resourceBundle.getString("report.report-id")+" <b>" + report.getIdReport() + "</b>" +
                            "\n"+resourceBundle.getString("report.latitude")+" <b>" + report.getLatitude() + "</b>" +
                            "\n"+resourceBundle.getString("report.longitude")+" <b>" + report.getLongitude() + "</b>" +
                            "\n"+resourceBundle.getString("report.product.name")+" <b>" + report.getLocaleName() + "</b>" +
                            "\n"+resourceBundle.getString("report.product.scarcity.level")+" <b>" + report.getProductScarcity() + "</b>" +
                            "\n\n===================================\n" +
                            "\n"+resourceBundle.getString("enter.expensive.level")+"\n" +
                            resourceBundle.getString("level.high")+"\n" +
                            resourceBundle.getString("level.normal");

                    shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update, messageText));
                    shortageTrackerBot.execute(generalHandler.levelInlineKeyboardEdit(update, PriceValueDetail.expensiveValueMap, report.getIdReport()));

                } catch (UserNotFoundException e) {
                    shortageTrackerBot.execute(generalHandler.decisionInlineKeyboardEdit(update));
                    shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update, "Receiver do not have address created\n" +
                            "\"User not found \n" +
                            "Please  send message  \"/start\""));
                } catch (ReportNotFoundException e) {
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
