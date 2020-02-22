package com.anuj.telegrambot.handler;

import com.anuj.telegrambot.bot.ShortageTrackerBot;
import com.anuj.telegrambot.contant.RegexConstant;
import com.anuj.telegrambot.exception.ReportNotFoundException;
import com.anuj.telegrambot.exception.UserNotFoundException;
import com.anuj.telegrambot.model.db.Report;
import com.anuj.telegrambot.model.db.User;
import com.anuj.telegrambot.repository.ReportRepository;
import com.anuj.telegrambot.repository.UserRepository;
import com.anuj.telegrambot.service.ReportService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NoteHandler {

    private final UserService userService;
    private final ReportRepository reportRepository;
    private final ReportService reportService;
    private final LocaleUtils localeUtils;
    private final ShortageTrackerBot shortageTrackerBot;
    private final GeneralHandler generalHandler;

    @Autowired
    public NoteHandler(UserService userService,
                       ReportRepository reportRepository,
                       ReportService reportService,
                       LocaleUtils localeUtils,
                       @Lazy ShortageTrackerBot shortageTrackerBot,
                       GeneralHandler generalHandler) {
        this.userService = userService;
        this.reportRepository = reportRepository;
        this.reportService = reportService;
        this.localeUtils = localeUtils;
        this.shortageTrackerBot = shortageTrackerBot;
        this.generalHandler = generalHandler;

    }

    public void noteSkip(Update update) {
        try {
            String callbackText = update.getCallbackQuery().getData();
            Integer telegramUserId = update.getCallbackQuery().getFrom().getId();
            Pattern pattern = Pattern.compile(RegexConstant.SKIP_NOTE);
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
                    report.setProductNote("");
                    report = reportRepository.save(report);
                    ResourceBundle resourceBundle = localeUtils.getMessageResource(user.getLanguageType());
                    String messageText = saveReportAndReturnMessage(user, report);
                    shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update,messageText));
                    shortageTrackerBot.execute(generalHandler.addSendOrCancel(resourceBundle,update,report.getIdReport()));

                }catch (UserNotFoundException e){
                    shortageTrackerBot.execute(generalHandler.decisionInlineKeyboardEdit(update));
                    shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update, "Receiver do not have address created\n" +
                            "\"User not found \n" +
                            "Please  send message  \"/start\""));
                }catch (ReportNotFoundException e){
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


    public SendMessage setNote(Update update) {
        Long chatId = update.getMessage().getChatId();
        try {
            Integer telegramUserId = update.getMessage().getFrom().getId();
            Pattern pattern = Pattern.compile(RegexConstant.PRODUCT_NOTE_FORMAT);
            Matcher matcher = pattern.matcher(update.getMessage().getText());
            String idReport = "";
            String noteString = "";
            boolean matched = false;
            while (matcher.find()) {
                matched = true;
                idReport = matcher.group(1);
                noteString = matcher.group(2);
            }
            if (matched) {
                try {
                    Long verifiedReportId = Long.valueOf(idReport);
                    User user = userService.getUserFromTelegramId(telegramUserId);
                    Report report = reportService.getReport(verifiedReportId, user);
                    ResourceBundle resourceBundle = localeUtils.getMessageResource(user.getLanguageType());
                    report.setProductNote(noteString);
                    report = reportRepository.save(report);
                    String messageText = saveReportAndReturnMessage(user, report);
                    SendMessage sendMessage = new SendMessage()
                            .setChatId(chatId)
                            .setText(messageText);
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText(resourceBundle.getString("send.yes")).setCallbackData("send_report_true_" + report.getIdReport()));
                    rowInline.add(new InlineKeyboardButton().setText(resourceBundle.getString("send.no")).setCallbackData("send_report_false_" + report.getIdReport()));
                    rowsInline.add(rowInline);
                    markupInline.setKeyboard(rowsInline);
                    sendMessage.setReplyMarkup(markupInline);
                    sendMessage.enableHtml(true);
                    return sendMessage;


                } catch (UserNotFoundException e) {
                    return new SendMessage().setChatId(chatId)
                            .setText("User not found \nPlease  send message  \"/start\"");
                } catch (ReportNotFoundException e) {
                    e.printStackTrace();
                    return new SendMessage().setChatId(chatId)
                            .setText("Report not found \nPlease  send message  \"/start\"");

                }
            } else {
                return new SendMessage() // Create a message object object
                        .setChatId(update.getMessage().getChatId())
                        .setText("Invalid syntax format");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SendMessage()
                .setChatId(chatId)
                .setText("Something went wrong. Please try again later");
    }

    public String saveReportAndReturnMessage(User user, Report report) {

        ResourceBundle resourceBundle = localeUtils.getMessageResource(user.getLanguageType());
        return "<b>" + resourceBundle.getString("report.your-report") + "</b> \n" +
                "\n===================================\n" +
                "\n" + resourceBundle.getString("report.report-id") + " <b>" + report.getIdReport() + "</b>" +
                "\n" + resourceBundle.getString("report.latitude") + " <b>" + report.getLatitude() + "</b>" +
                "\n" + resourceBundle.getString("report.longitude") + " <b>" + report.getLongitude() + "</b>" +
                "\n" + resourceBundle.getString("report.product.name") + " <b>" + report.getLocaleName() + "</b>" +
                "\n" + resourceBundle.getString("report.product.scarcity.level") + " <b>" + report.getProductScarcity() + "</b>" +
                "\n" + resourceBundle.getString("report.product.expensive.level") + " <b>" + report.getProductPrice() + "</b>" +
                "\n" + resourceBundle.getString("report.product.note") + " <b><i>" + report.getProductNote() + "</i>" + "</b>" +
                "\n\n===================================\n" +
                resourceBundle.getString("enter.submit.decision");

    }

}