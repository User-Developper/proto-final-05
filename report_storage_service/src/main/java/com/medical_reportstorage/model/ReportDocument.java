package com.medical_reportstorage.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "report_images")
public class ReportDocument {

    @Id
    private String id;

    private Integer reportId;

    private String fileName;

    private String contentType;

    private String storageName;

    private Long fileSize;

    private LocalDateTime createdAt;

    public ReportDocument() {
    }

    public ReportDocument(
            Integer reportId,
            String fileName,
            String contentType,
            String storageName,
            Long fileSize,
            LocalDateTime createdAt) {
        this.reportId = reportId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.storageName = storageName;
        this.fileSize = fileSize;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
