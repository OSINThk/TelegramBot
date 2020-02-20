package com.anuj.telegrambot.service;

import com.anuj.telegrambot.contant.Urls;
import com.anuj.telegrambot.exception.ProductNotFoundException;
import com.anuj.telegrambot.model.db.Product;
import com.anuj.telegrambot.model.db.User;
import com.anuj.telegrambot.model.dto.ProductDto;
import com.anuj.telegrambot.model.dto.ProductListDto;
import com.anuj.telegrambot.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final RestTemplate restTemplate;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          RestTemplate restTemplate) {
        this.productRepository = productRepository;
        this.restTemplate = restTemplate;
    }


    public List<ProductDto> getProductList() {

        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        ResponseEntity<ProductDto[]> productListResponseEntity = restTemplate.exchange(Urls.PRODUCT_FETCH_URL, HttpMethod.GET, httpEntity, ProductDto[].class);
        if (productListResponseEntity.getStatusCode() == HttpStatus.OK) {
            return Arrays.stream(productListResponseEntity.getBody()).collect(Collectors.toList());
        }
        return new ProductListDto().getProductDtoList();

    }

    public void saveProductListIfNotPresent(List<ProductDto> productDtoList) {
        for (ProductDto productDto : productDtoList) {
            Optional<Product> productOptional = productRepository.findByServerProductId(productDto.getServerProductId());
            if(!productOptional.isPresent()){
                Product product = Product.getProductFromDto(productDto);
                productRepository.save(product);
            }
        }
    }

    public LinkedHashMap<String, String> getProductListFromDatabase(){
        List<Product> productList = productRepository.findAll();
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        for(Product product: productList){
            linkedHashMap.put("prod_"+product.getIdProduct(),product.getEnglish());
        }

        return linkedHashMap;
    }

    public Product getProduct(Long idProduct) throws ProductNotFoundException {
        Optional<Product> productOptional = productRepository.findById(idProduct);
        return productOptional.orElseThrow(ProductNotFoundException::new);
    }

    public String getProductLocaleName(Product product, User user){
        switch (user.getLanguageType()){
            case English:{
                return product.getEnglish();
            }
            case 简体:{
                return product.getSimplified();
            }
            case 繁体:{
                return product.getTraditional();
            }
            case 繁体_台灣:{
                return product.getTraditionalTaiwan();
            }
            default:{
                return "";
            }

        }
    }

}
