package com.anuj.telegrambot.handler;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SubmissionHandler {

    public static AnswerCallbackQuery checkSubmissionDecision(Update update){
        CallbackQuery callbackQuery = update.getCallbackQuery();
        if(callbackQuery.getData().equals("submit_true")){
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
            answerCallbackQuery.setText("Information successfully submitted");
            answerCallbackQuery.setShowAlert(true);
            return answerCallbackQuery;

        }else{
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
            answerCallbackQuery.setText("Submission Canceled");
            answerCallbackQuery.setShowAlert(true);
            return answerCallbackQuery;

        }
    }

}
