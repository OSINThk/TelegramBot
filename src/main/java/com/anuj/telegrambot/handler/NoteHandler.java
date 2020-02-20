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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NoteHandler {

    private final UserService userService;
    private final ReportRepository reportRepository;
    private final ReportService reportService;

    @Autowired
    public NoteHandler(UserService userService,
                       ReportRepository reportRepository,
                       ReportService reportService) {
        this.userService = userService;
        this.reportRepository = reportRepository;
        this.reportService = reportService;

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
                    report.setProductNote(noteString);
                    report = reportRepository.save(report);
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
                            "Do you want to submit this record?";
                    SendMessage sendMessage = new SendMessage()
                            .setChatId(chatId)
                            .setText(messageText);
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText("Yes").setCallbackData("send_report_true_"+report.getIdReport()));
                    rowInline.add(new InlineKeyboardButton().setText("No").setCallbackData("send_report_false_"+report.getIdReport()));
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

}