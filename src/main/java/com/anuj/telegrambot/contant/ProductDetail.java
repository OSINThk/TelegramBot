package com.anuj.telegrambot.contant;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProductDetail {

    public static LinkedHashMap<String, String> productMap;
    static {
        productMap = new LinkedHashMap<>();
        productMap.put("prod_fresh_vegetables","Fresh Vegetables");
        productMap.put("prod_milk","Milk");
        productMap.put("prod_meat","Meat");
        productMap.put("prod_rice","Rice");
        productMap.put("prod_disinfectant","Disinfectant");
        productMap.put("prod_soap","Soap");
        productMap.put("prod_laundry_detergent","Laundry Detergent");
        productMap.put("prod_instant_noodles","Noodles");
        productMap.put("prod_bread","Bread");
        productMap.put("prod_pasta","Pasta");
        productMap.put("prod_processed_meat","Processed Meat");
        productMap.put("prod_latex_gloves","Latex Gloves");
        productMap.put("prod_hand_sanitizer","Hand Sanitizer");
        productMap.put("prod_face_masks","Face Masks");
        productMap.put("prod_iospropyl_alcohol","IsoPropyl Alcohol");
        productMap.put("prod_bleach","Bleach");
        productMap.put("prod_alcohol_wipes","Alcohol Wipes");
        productMap.put("prod_paper_towel","Paper Towel");
        productMap.put("prod_toilet_paper","Toilet Paper");
        productMap.put("prod_cat_dog_food","Cat/Dog Food");

    }

}
