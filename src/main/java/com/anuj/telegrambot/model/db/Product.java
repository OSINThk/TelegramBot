package com.anuj.telegrambot.model.db;

import com.anuj.telegrambot.model.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "id_product")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduct;

    @Column(name = "server_product_id")
    private Long serverProductId;

    @Column(name = "name")
    private String productName;

    @Column(name = "english")
    private String english;

    @Column(name = "simplified")
    private String simplified;

    @Column(name = "traditional")
    private String traditional;

    @Column(name = "traditional_taiwan")
    private String traditionalTaiwan;

    public static Product getProductFromDto(ProductDto productDto){
        Product product = new Product();
        product.setServerProductId(productDto.getServerProductId());
        product.setProductName(productDto.getProductName());
        product.setEnglish(productDto.getLocalizationDto().getEnglish());
        product.setSimplified(productDto.getLocalizationDto().getSimplified());
        product.setTraditional(productDto.getLocalizationDto().getTraditional());
        product.setTraditionalTaiwan(productDto.getLocalizationDto().getTraditionalTaiwan());
        return product;
    }



}
