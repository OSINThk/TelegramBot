package com.anuj.telegrambot.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {

    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("product_scarcity")
    private int productScarcity;
    @JsonProperty("product_price")
    private int productPrice;
    @JsonProperty("product_note")
    private String productNote;
    @JsonProperty("latitude")
    private double latitude;
    @JsonProperty("longitude")
    private double longitude;
    @JsonProperty("report_chat_id")
    private Long reportChatId;


}
