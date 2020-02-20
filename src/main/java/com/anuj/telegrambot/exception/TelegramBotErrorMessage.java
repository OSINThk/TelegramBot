package com.anuj.telegrambot.exception;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Accessors(fluent = true)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ApiModel(parent = TelegramBotException.class, description = "Class representing telegram bot general error")
public class TelegramBotErrorMessage extends TelegramBotException {
    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    private String code = "ERROR_OCCURRED";
    private String error="Error Occurred";
    private String desc;
    private String data;

    public TelegramBotErrorMessage(String errorMessage){
        this.error = errorMessage;
    }
}
