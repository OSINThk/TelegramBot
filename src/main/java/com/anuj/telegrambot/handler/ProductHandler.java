package com.anuj.telegrambot.handler;

import com.anuj.telegrambot.bot.ShortageTrackerBot;
import com.anuj.telegrambot.contant.RegexConstant;
import com.anuj.telegrambot.contant.ScarcityValueDetail;
import com.anuj.telegrambot.exception.ProductNotFoundException;
import com.anuj.telegrambot.exception.ReportNotFoundException;
import com.anuj.telegrambot.exception.UserNotFoundException;
import com.anuj.telegrambot.model.db.Product;
import com.anuj.telegrambot.model.db.Report;
import com.anuj.telegrambot.model.db.User;
import com.anuj.telegrambot.repository.ReportRepository;
import com.anuj.telegrambot.repository.UserRepository;
import com.anuj.telegrambot.service.ProductService;
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
public class ProductHandler {

    private final UserService userService;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final ShortageTrackerBot shortageTrackerBot;
    private final GeneralHandler generalHandler;
    private final ReportService reportService;

    @Autowired
    public ProductHandler(UserService userService,
                          ReportRepository reportRepository,
                          UserRepository userRepository,
                          ProductService productService,
                          @Lazy ShortageTrackerBot shortageTrackerBot,
                          GeneralHandler generalHandler,
                          ReportService reportService) {
        this.userRepository = userRepository;
        this.productService = productService;
        this.userService = userService;
        this.reportRepository = reportRepository;
        this.shortageTrackerBot = shortageTrackerBot;
        this.generalHandler = generalHandler;
        this.reportService = reportService;
    }

    public void setProduct(Update update) {
        try {

            String callbackText = update.getCallbackQuery().getData();
            Integer telegramUserId = update.getCallbackQuery().getFrom().getId();
            Pattern pattern = Pattern.compile(RegexConstant.PRODUCT_CATEGORY_FORMAT);
            Matcher matcher = pattern.matcher(callbackText);
            String idProduct = "";
            String idReport = "";
            boolean matched = false;
            while (matcher.find()) {
                matched = true;
                idProduct = matcher.group(1);
                idReport = matcher.group(2);
            }
            if (matched) {
                try {
                    Long verifiedIdProduct = Long.valueOf(idProduct);
                    Long verifiedIdReport = Long.valueOf(idReport);
                    User user = userService.getUserFromTelegramId(telegramUserId);
                    Product product = productService.getProduct(verifiedIdProduct);
                    Report report = reportService.getReport(verifiedIdReport, user);
                    report.setProduct(product);
                    report.setLocaleName(productService.getProductLocaleName(product, user));
                    reportRepository.save(report);
                    shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update, "You have selected: \n" +
                            "\nProduct Name: " + report.getLocaleName()+"\n" +
                            "\n===================================\n"+
                            "Please Select the scarcity level\n" +
                            "5: High\n" +
                            "1: Normal"));
                    shortageTrackerBot.execute(generalHandler.levelInlineKeyboardEdit(update,ScarcityValueDetail.scarcityValueMap,report.getIdReport()));


                } catch (UserNotFoundException e) {
                    shortageTrackerBot.execute(generalHandler.decisionInlineKeyboardEdit(update));
                    shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update, "Receiver do not have address created\n" +
                            "\"User not found \n" +
                            "Please  send message  \"/start\""));

                } catch (ProductNotFoundException e) {
                    shortageTrackerBot.execute(generalHandler.decisionInlineKeyboardEdit(update));
                    shortageTrackerBot.execute(generalHandler.decisionMessageEdit(update, "Something went wrong. Please try again later"));
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
