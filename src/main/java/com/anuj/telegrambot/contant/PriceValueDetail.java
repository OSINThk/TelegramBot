package com.anuj.telegrambot.contant;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PriceValueDetail {

    public static LinkedHashMap<String, Integer> expensiveValueMap;

    static {
        expensiveValueMap = new LinkedHashMap<>();
        expensiveValueMap.put("expensive_one", 1);
        expensiveValueMap.put("expensive_two", 2);
        expensiveValueMap.put("expensive_three", 3);
        expensiveValueMap.put("expensive_four", 4);
        expensiveValueMap.put("expensive_five", 5);
    }


}
