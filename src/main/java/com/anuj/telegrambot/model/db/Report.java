package com.anuj.telegrambot.model.db;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    private Long id;
    private Integer telegramUserId;
    private String productName;
    private int productScarcity;
    private int productPrice;
    private String productNotes;
    private double latitude;
    private double longitude;



}

