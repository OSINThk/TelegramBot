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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SendReportHandler {

    private final UserService userService;
    private final ReportRepository reportRepository;
    private final ReportService reportService;
    private final ShortageTrackerBot shortageTrackerBot;
    private final GeneralHandler generalHandler;


    @Autowired
    public SendReportHandler(UserService userService,
                             ReportRepository reportRepository,
                             ReportService reportService,
                             @Lazy ShortageTrackerBot shortageTrackerBot,
                             GeneralHandler generalHandler) {
        this.userService = userService;
        this.reportRepository = reportRepository;
        this.reportService = reportService;
        this.shortageTrackerBot = shortageTrackerBot;
        this.generalHandler = generalHandler;
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
                    Report report = reportService.getReport(verifiedReportId,user);
                    //todo submit report to server
                    report.setIsSubmitted(true);
                    reportRepository.save(report);
                    String messageText =  "<b>Your Report:</b> \n" +
                            "\n===================================\n"+
                            "\nReport Id: <b>" + report.getIdReport() +"</b>"+
                            "\nLatitude: <b>"+report.getLatitude()+"</b>"+
                            "\nLongitude: <b>"+report.getLongitude()+"</b>"+
                            "\nProduct Name: <b>" + report.getLocaleName() +"</b>"+
                            "\nProduct Scarcity Level: <b>" + report.getProductScarcity() +"</b>"+
                            "\nProduct Expensive Level: <b>" + report.getProductPrice() +"</b>"+
                            "\nProduct Note:<b><i>" + report.getProductNote() + "</i>" +"</b>"+
                            "\n\n===================================\n" +
                            "Successfully Submitted";
                    shortageTrackerBot.execute(generalHandler.decisionInlineKeyboardEdit(update));
                    shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update,messageText));
                }catch (UserNotFoundException | ReportNotFoundException e){
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

    public void sendReportCancel(Update update){
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
                    Report report = reportService.getReport(verifiedReportId,user);
                    reportRepository.delete(report);
                    shortageTrackerBot.execute(generalHandler.decisionInlineKeyboardEdit(update));
                    shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update, "Report Canceled."));
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
