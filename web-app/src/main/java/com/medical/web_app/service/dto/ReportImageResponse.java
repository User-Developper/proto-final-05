package com.medical.web_app.service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportImageResponse {

    private String id;

    private Integer reportId;

    private String fileName;

    private String contentType;

    private String storageName;

    private Long fileSize;

    private String storagePath;

    private LocalDateTime createdAt;
}