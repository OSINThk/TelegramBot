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
public class SendReportHandler {

    private final UserService userService;
    private final ReportRepository reportRepository;
    private final ReportService reportService;
    private final ShortageTrackerBot shortageTrackerBot;
    private final GeneralHandler generalHandler;
    private final LocaleUtils localeUtils;


    @Autowired
    public SendReportHandler(UserService userService,
                             ReportRepository reportRepository,
                             ReportService reportService,
                             @Lazy ShortageTrackerBot shortageTrackerBot,
                             GeneralHandler generalHandler,
                             LocaleUtils localeUtils) {
        this.userService = userService;
        this.reportRepository = reportRepository;
        this.reportService = reportService;
        this.shortageTrackerBot = shortageTrackerBot;
        this.generalHandler = generalHandler;
        this.localeUtils = localeUtils;
    }

    public void sendReportConfirm(Update update) {
        try {
            String callbackText = update.getCallbackQuery().getData();
            Integer telegramUserId = update.getCallbackQuery().getFrom().getId();
            Pattern pattern = Pattern.compile(RegexConstant.SEND_REPORT_ACCEPT);
            Matcher matcher = pattern.matcher(callbackText);
            String idReport = "";
            boolean matched = false;
            while (matcher.find()) {
                matched = true;
                idReport = matcher.group(1);
            }
            if (matched) {
                try {
                    Long verifiedReportId = Long.valueOf(idReport);
                    User user = userService.getUserFromTelegramId(telegramUserId);
                    Report report = reportService.getReport(verifiedReportId, user);
                    ResourceBundle resourceBundle = localeUtils.getMessageResource(user.getLanguageType());
                    String messageText = "";
                    if (reportService.postReport(report)) {
                        report.setIsSubmitted(true);
                        reportRepository.save(report);

                        messageText = "<b>" + new String(resourceBundle.getString("report.your-report").getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8) + "</b> \n" +
                                "\n===================================\n" +
                                "\n" + new String(resourceBundle.getString("report.report-id").getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8) + " <b>" + report.getIdReport() + "</b>" +
                                "\n" + new String(resourceBundle.getString("report.latitude").getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8) + " <b>" + report.getLatitude() + "</b>" +
                                "\n" + new String(resourceBundle.getString("report.longitude").getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8) + " <b>" + report.getLongitude() + "</b>" +
                                "\n" + new String(resourceBundle.getString("report.product.name").getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8) + " <b>" + report.getLocaleName() + "</b>" +
                                "\n" + new String(resourceBundle.getString("report.product.scarcity.level").getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8) + " <b>" + report.getProductScarcity() + "</b>" +
                                "\n" + new String(resourceBundle.getString("report.product.expensive.level").getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8) + " <b>" + report.getProductPrice() + "</b>" +
                                "\n" + new String(resourceBundle.getString("report.product.note").getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8) + " <b><i>" + report.getProductNote() + "</i>" + "</b>" +
                                "\n\n===================================\n" +
                                new String(resourceBundle.getString("send.successful").getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);

                    } else {
                        messageText = new String(resourceBundle.getString("send.canceled").getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
                    }
                    shortageTrackerBot.execute(generalHandler.decisionInlineKeyboardEdit(update));
                    shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update, messageText));
                } catch (UserNotFoundException | ReportNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                shortageTrackerBot.execute(generalHandler.decisionInlineKeyboardEdit(update));
                shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update, "Something went wrong. Please try again later"));

            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendReportCancel(Update update) {
        try {
            String callbackText = update.getCallbackQuery().getData();
            Integer telegramUserId = update.getCallbackQuery().getFrom().getId();
            Pattern pattern = Pattern.compile(RegexConstant.SEND_REPORT_REJECT);
            Matcher matcher = pattern.matcher(callbackText);
            String idReport = "";
            boolean matched = false;
            while (matcher.find()) {
                matched = true;
                idReport = matcher.group(1);
            }
            if (matched) {
                try {
                    Long verifiedReportId = Long.valueOf(idReport);
                    User user = userService.getUserFromTelegramId(telegramUserId);
                    ResourceBundle resourceBundle = localeUtils.getMessageResource(user.getLanguageType());
                    Report report = reportService.getReport(verifiedReportId, user);
                    reportRepository.delete(report);
                    shortageTrackerBot.execute(generalHandler.decisionInlineKeyboardEdit(update));
                    shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update, new String(resourceBundle.getString("send.canceled").getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8)));
                } catch (UserNotFoundException e) {

                } catch (ReportNotFoundException e) {
                    e.printStackTrace();
                }

            } else {
                shortageTrackerBot.execute(generalHandler.decisionInlineKeyboardEdit(update));
                shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update, "Something went wrong. Please try again later"));
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
            ;
        }
    }

}
