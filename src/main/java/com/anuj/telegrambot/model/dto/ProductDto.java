package com.anuj.telegrambot.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    @JsonProperty("id")
    private Long serverProductId;
    @JsonProperty("name")
    private String productName;
    @JsonProperty("localization")
    private LocalizationDto localizationDto;

}
