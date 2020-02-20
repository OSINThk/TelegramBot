package com.anuj.telegrambot.exception;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Data
@ApiModel(subTypes = {TelegramBotErrorMessage.class}, discriminator = "type",
        description = "Supertype of all error thrown.")
public abstract class TelegramBotException extends Exception {
    public abstract HttpStatus httpStatus();
    public  abstract String code();
    public abstract String error();
    public abstract String desc();
    public abstract String data();

}
