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

import java.nio.charset.StandardCharsets;
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
                    String messageText = "<b>" + new String(resourceBundle.getString("report.your-report").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8) + "</b> \n" +
                            "\n===================================\n" +
                            "\n" + new String(resourceBundle.getString("report.report-id").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8) + " <b>" + report.getIdReport() + "</b>" +
                            "\n" + new String(resourceBundle.getString("report.latitude").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8) + " <b>" + report.getLatitude() + "</b>" +
                            "\n" + new String(resourceBundle.getString("report.longitude").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8) + " <b>" + report.getLongitude() + "</b>" +
                            "\n" + new String(resourceBundle.getString("report.product.name").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8) + " <b>" + report.getLocaleName() + "</b>" +
                            "\n" + new String(resourceBundle.getString("report.product.scarcity.level").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8) + " <b>" + report.getProductScarcity() + "</b>" +
                            "\n\n===================================\n" +
                            "\n" + new String(resourceBundle.getString("enter.expensive.level").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8) + "\n" +
                            new String(resourceBundle.getString("level.high").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8) + "\n" +
                            new String(resourceBundle.getString("level.normal").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

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
