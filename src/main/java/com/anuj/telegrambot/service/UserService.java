package com.anuj.telegrambot.service;

import com.anuj.telegrambot.exception.UserNotFoundException;
import com.anuj.telegrambot.model.db.User;
import com.anuj.telegrambot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User getUserFromTelegramId(Integer telegramUserId) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findByTelegramUserId(telegramUserId);
        return userOptional.orElseThrow(UserNotFoundException::new);
    }





}
