package com.anuj.telegrambot.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;


@Accessors(fluent = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class UserNotFoundException extends TelegramBotException {
    private HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    private String code = "USER_NOT_FOUND";
    private String error="User not found";
    private String desc;
    private String data;

}
