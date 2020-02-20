package com.anuj.telegrambot.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalizationDto {

    @JsonProperty("en")
    private String english;
    @JsonProperty("zh-CN")
    private String simplified;
    @JsonProperty("zh-HK")
    private String traditional;
    @JsonProperty("zh-TW")
    private String traditionalTaiwan;


}
