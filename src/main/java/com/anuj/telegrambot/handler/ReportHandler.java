package com.anuj.telegrambot.handler;

import com.anuj.telegrambot.repository.UserRepository;
import com.anuj.telegrambot.utils.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportHandler {

    private final UserRepository userRepository;
    private final LocaleUtils localeUtils;

    @Autowired
    public ReportHandler(UserRepository userRepository,
                        LocaleUtils localeUtils) {
        this.userRepository = userRepository;
        this.localeUtils = localeUtils;

    }


}
