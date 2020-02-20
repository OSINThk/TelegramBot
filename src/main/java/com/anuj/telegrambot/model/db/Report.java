package com.anuj.telegrambot.model.db;


import com.anuj.telegrambot.model.dto.ReportDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "report")
public class Report {

    @Id
    @Column(name = "id_report")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReport;

    @OneToOne
    private Product product;

    @Column(name = "product_scarcity")
    private int productScarcity;

    @Column(name = "product_price")
    private int productPrice;

    @Column(name = "product_note")
    private String productNote;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "locale_name")
    private String localeName;

    @ManyToOne
    @JoinColumn(name = "fk_report_user")
    private User user;

    @Column(name = "is_submitted")
    private Boolean isSubmitted = false;

}

