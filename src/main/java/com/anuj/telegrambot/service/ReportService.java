package com.anuj.telegrambot.service;

import com.anuj.telegrambot.exception.ReportNotFoundException;
import com.anuj.telegrambot.model.db.Report;
import com.anuj.telegrambot.model.db.User;
import com.anuj.telegrambot.model.dto.ReportDto;
import com.anuj.telegrambot.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }


    public Report getReport(Long idReport, User user) throws ReportNotFoundException {
        Optional<Report> reportOptional = reportRepository.findByIdReportAndUser(idReport, user);
        return reportOptional.orElseThrow(ReportNotFoundException::new);
    }


    public boolean  postReport(Report report) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Cookie", "_shortage_tracker_session=RyrlCP5yW63NZPDzremeuS9JENhZ%2FhlvZWYBr2UKQPQdFpQurNS9U211s4tMAlQHTR8khI2ntfD9Lvn4NEGU%2BkI5Doky2HgREhywKDcKxK3MaNvFfsNDCqsiJjsdN8wXL9TrcpjwK1kxhS8fLwzHFpHOR7PnHVERjK0k08gIRQoXJ8AmEKcpOEDSf%2FrYoXLC1E2V5gHDDRxOt6H3ttGLUTvpg8U16b4HIg9DZuX6TZpmmrtOKUERa0H%2FwM6M2Qy6wshYtuiQ3yyjHH1uS5jwMA0BhXRX%2BRxcPZrQe3FPxhdIFK1mXjDSqHP8MIxgsJqsAMG84DyezqZlCdTjtgUcg1E5CLmScynafjBfG%2Bw5ullNDO4%2BzH2EGEFxKUAigt8sojD%2B%2ByNp0uPlMCdW70DvH3NMIixqK5A6Pl%2FpzE6TDpsqodZvHvsZWM6iYTk45m4b%2BpN04UZCUxUKgJSfRlpJPv62duh6--gzSo0fmDvoXGCmmA--zhmmynHl4NRyTE0TzSRC5g%3D%3D");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("report[lat]", String.valueOf(report.getLatitude()));
        map.add("report[long]", String.valueOf(report.getLongitude()));
        map.add("report[notes]", "");
        map.add("report[product_detail_attributes][0][_destroy]", "false");
        map.add("report[product_detail_attributes][0][product_id]", String.valueOf(report.getProduct().getServerProductId()));
        map.add("report[product_detail_attributes][0][scarcity]", String.valueOf(report.getProductScarcity()));
        map.add("report[product_detail_attributes][0][price]", String.valueOf(report.getProductPrice()));
        map.add("report[product_detail_attributes][0][notes]", report.getProductNote());
        map.add("commit", "Create Report");
        map.add("authenticity_token", "K//bwuRJYwBPtprspcnNFrt1rSF56MuGo+HdhwLYgJ+39J1SI6gp1lpu7y0xVPh4YvdR8y2hI8lmSPzUV5/l5g==");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity("https://shortage-tracker.osinthk.org/en/reports", request, String.class);
        return response.getStatusCode() == HttpStatus.FOUND;
    }

}
