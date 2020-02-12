package com.anuj.telegrambot.contant;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScarcityValueDetail {

    public static LinkedHashMap<String, Integer> scarcityValueMap;

    static {
        scarcityValueMap = new LinkedHashMap<>();
        scarcityValueMap.put("scarcity_one", 1);
        scarcityValueMap.put("scarcity_two", 2);
        scarcityValueMap.put("scarcity_three", 3);
        scarcityValueMap.put("scarcity_four", 4);
        scarcityValueMap.put("scarcity_five", 5);
    }

}
