package com.anuj.telegrambot.repository;

import com.anuj.telegrambot.model.db.Report;
import com.anuj.telegrambot.model.db.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report,Long> {

    Optional<Report> findByIdReportAndUser(Long idReport, User user);

}
