package com.anuj.telegrambot.repository;

import com.anuj.telegrambot.model.db.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByTelegramUserId(Integer telegramUserId);


}
