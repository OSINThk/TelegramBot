package com.anuj.telegrambot.model.dto;

import com.anuj.telegrambot.contant.LanguageType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @JsonProperty("telegram_user_id")
    private Integer telegramUserId;
    @JsonProperty("telegram_user_name")
    private String telegramUserName;
    @JsonProperty("language_type")
    private LanguageType languageType;



}
