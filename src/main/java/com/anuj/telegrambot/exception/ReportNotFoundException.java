package com.anuj.telegrambot.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Accessors(fluent = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportNotFoundException extends TelegramBotException{
    private HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    private String code = "REPORT_NOT_FOUND";
    private String error="Report not found";
    private String desc;
    private String data;

}
