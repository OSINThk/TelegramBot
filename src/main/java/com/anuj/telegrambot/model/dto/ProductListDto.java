package com.anuj.telegrambot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ProductListDto {

    private List<ProductDto> productDtoList;

    public ProductListDto(){
        productDtoList = new ArrayList<>();
    }

}
