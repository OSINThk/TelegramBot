package com.anuj.telegrambot.contant;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScarcityValueDetail {

    public static LinkedHashMap<String, Integer> scarcityValueMap;

    static {
        scarcityValueMap = new LinkedHashMap<>();
        scarcityValueMap.put("scarcity_1", 1);
        scarcityValueMap.put("scarcity_2", 2);
        scarcityValueMap.put("scarcity_3", 3);
        scarcityValueMap.put("scarcity_4", 4);
        scarcityValueMap.put("scarcity_5", 5);
    }

}
