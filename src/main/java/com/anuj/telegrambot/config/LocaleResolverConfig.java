package com.anuj.telegrambot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "start")
@Getter
@Setter
public class LocaleResolverConfig {

    private  String askLocation;

}
