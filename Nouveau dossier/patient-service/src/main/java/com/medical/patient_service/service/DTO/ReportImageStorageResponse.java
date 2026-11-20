
package com.medical.patient_service.service.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportImageStorageResponse {

    private String id;
    private Integer reportId;
    private String fileName;
    private String contentType;
    private String storageName;
    private Long fileSize;
    private LocalDateTime createdAt;
}
