package com.anuj.telegrambot.service;

import com.anuj.telegrambot.exception.ReportNotFoundException;
import com.anuj.telegrambot.model.db.Report;
import com.anuj.telegrambot.model.db.User;
import com.anuj.telegrambot.repository.ReportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }


    public Report getReport(Long idReport, User user) throws ReportNotFoundException {
        Optional<Report> reportOptional = reportRepository.findByIdReportAndUser(idReport, user);
        return reportOptional.orElseThrow(ReportNotFoundException::new);
    }


    public boolean postReport(Report report) {

        HttpHeaders headers = new HttpHeaders();

        headers.add("content-type", "multipart/form-data");
        headers.add("X-Telegram-Bot-Key", "supersecretkey");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("report[lat]", String.valueOf(report.getLatitude()));
        map.add("report[long]", String.valueOf(report.getLongitude()));
        map.add("report[notes]", "");
        map.add("report[product_detail_attributes][0][_destroy]", "false");
        map.add("report[product_detail_attributes][0][product_id]", String.valueOf(report.getProduct().getServerProductId()));
        map.add("report[product_detail_attributes][0][scarcity]", String.valueOf(report.getProductScarcity()));
        map.add("report[product_detail_attributes][0][price]", String.valueOf(report.getProductPrice()));
        map.add("report[product_detail_attributes][0][notes]", report.getProductNote());
        map.add("commit", "Create Report");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity("https://shortage-tracker.osinthk.org/en/create_bot_report.json", request, String.class);
        System.out.println(response.getBody());
        return response.getStatusCode().value() == HttpStatus.CREATED.value();

    }


}
