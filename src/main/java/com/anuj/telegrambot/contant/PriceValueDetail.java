package com.anuj.telegrambot.contant;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PriceValueDetail {

    public static LinkedHashMap<String, Integer> expensiveValueMap;

    static {
        expensiveValueMap = new LinkedHashMap<>();
        expensiveValueMap.put("expensive_1", 1);
        expensiveValueMap.put("expensive_2", 2);
        expensiveValueMap.put("expensive_3", 3);
        expensiveValueMap.put("expensive_4", 4);
        expensiveValueMap.put("expensive_5", 5);
    }


}
