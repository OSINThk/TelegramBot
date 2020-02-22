package com.anuj.telegrambot.utils;

import com.anuj.telegrambot.contant.LanguageType;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.ResourceBundle;

@Service
public class LocaleUtils {

    public ResourceBundle getMessageResource(LanguageType languageType){
        if(languageType.equals(LanguageType.zh_CN)){
            Locale locale = new Locale("zh","CN");
            return ResourceBundle.getBundle("message",locale);
        }else if(languageType.equals(LanguageType.zh_HK)){
            Locale locale = new Locale("zh","HK");
            return ResourceBundle.getBundle("message",locale);
        }else if(languageType.equals(LanguageType.zh_TW)){
            Locale locale = new Locale("zh","TW");
            return ResourceBundle.getBundle("message",locale);
        }else{
            Locale  locale = new Locale("en");
            return ResourceBundle.getBundle("message",locale);
        }

    }

}
