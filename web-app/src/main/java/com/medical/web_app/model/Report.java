package com.medical.web_app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;



public class Report {



    private Integer id;

    private String title;
    private String description;
    private String location;

    private LocalDate date;

    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private Gravity gravity;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Category category;

    // ✅ lien avec TON User existant
    @ManyToOne
    private User user;
}