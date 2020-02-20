package com.anuj.telegrambot.bot;

import com.anuj.telegrambot.contant.BotCommands;
import com.anuj.telegrambot.contant.BotTokens;
import com.anuj.telegrambot.handler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class ShortageTrackerBot extends TelegramLongPollingBot {

    private final StartHandler startHandler;
    private final LocationHandler locationHandler;
    private final ProductHandler productHandler;
    private final ScarcityHandler scarcityHandler;
    private final PriceHandler priceHandler;
    private final NoteHandler noteHandler;
    private final SendReportHandler sendReportHandler;

    @Autowired
    public ShortageTrackerBot(StartHandler startHandler,
                              LocationHandler locationHandler,
                              ProductHandler productHandler,
                              ScarcityHandler scarcityHandler,
                              PriceHandler priceHandler,
                              NoteHandler noteHandler,
                              SendReportHandler sendReportHandler){
        this.startHandler = startHandler;
        this.locationHandler = locationHandler;
        this.productHandler = productHandler;
        this.scarcityHandler = scarcityHandler;
        this.priceHandler = priceHandler;
        this.noteHandler = noteHandler;
        this.sendReportHandler = sendReportHandler;
    }

    public void onUpdateReceived(Update update) {
        try {
            if(update.hasCallbackQuery()){
                if(update.getCallbackQuery().getData().startsWith("prod")){
                    productHandler.setProduct(update);
                }else if(update.getCallbackQuery().getData().startsWith("scarcity")){
                    scarcityHandler.setScarcity(update);
                }else if (update.getCallbackQuery().getData().startsWith("expensive")){
                    priceHandler.setExpensiveness(update);
                }else if(update.getCallbackQuery().getData().startsWith("submit")){
                    execute(SubmissionHandler.checkSubmissionDecision(update));
                }else if(update.getCallbackQuery().getData().startsWith("send_report")){
                    if(update.getCallbackQuery().getData().startsWith("send_report_true")){
                        sendReportHandler.sendReportConfirm(update);
                    }else{
                        sendReportHandler.sendReportCancel(update);
                    }
                }
            }
            if(update.hasMessage()&& update.getMessage().getLocation()!=null){
                execute(locationHandler.setReportLocation(update));
            }
            if(update.hasMessage() && update.getMessage().hasText()){
                if(update.getMessage().getText().equals(BotCommands.START_COMMAND)){
                    execute(startHandler.startInformationCollection(update));
                }else if(update.getMessage().getText().startsWith("/note")){
                    execute(noteHandler.setNote(update));
                }
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public String getBotUsername() {
        return BotTokens.BOT_USER_NAME;
    }

    public String getBotToken() {
        return BotTokens.BOT_TOKEN;
    }
}
