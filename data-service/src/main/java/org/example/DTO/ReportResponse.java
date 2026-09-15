package org.example.DTO;

import lombok.Data;
import org.example.model.Category;
import org.example.model.Gravity;
import org.example.model.Status;

import java.time.LocalDate;

@Data
public class ReportResponse {

    private Integer id;

    private Long userId;

    private Integer clientId;

    private String title;

    private String description;

    private Category category;

    private Gravity gravity;

    private String location;

    private LocalDate date;

    private Double latitude;

    private Double longitude;

    private Status status;
}