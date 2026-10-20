package com.medical.patient_service.service.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportImageResponse {

    private Integer id;

    private Integer reportId;

    private String fileName;

    private String contentType;

    private String storageName;

    private Long fileSize;

    private String storagePath;

    private LocalDateTime createdAt;
}