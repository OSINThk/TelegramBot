package com.anuj.telegrambot.component;

import com.anuj.telegrambot.model.dto.ProductDto;
import com.anuj.telegrambot.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductScheduler {

    private final ProductService productService;

    @Autowired
    public ProductScheduler(ProductService productService){
        this.productService = productService;
    }

    @Scheduled(fixedRate = 1000*60*60)
    public void getProductFromServer(){
        List<ProductDto> productDtoList = productService.getProductList();
        productService.saveProductListIfNotPresent(productDtoList);
    }

}
