
package com.medical_reportstorage.controller;

import com.medical_reportstorage.model.ReportDocument;
import com.medical_reportstorage.service.ReportService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping(
            value = "/{reportId}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ReportDocument> uploadImage(
            @PathVariable Integer reportId,
            @RequestParam("file") MultipartFile file)
            throws IOException {

        ReportDocument savedImage =
                reportService.storeImage(reportId, file);

        return ResponseEntity.ok(savedImage);
    }

    @GetMapping("/{reportId}/images")
    public ResponseEntity<List<ReportDocument>> getImages(
            @PathVariable Integer reportId) {

        return ResponseEntity.ok(
                reportService.getImagesByReportId(reportId)
        );
    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String imageId) {

        var image = reportService.getImage(imageId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        image.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                                image.getFilename() + "\""
                )
                .body(image);
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable String imageId) {

        reportService.deleteImage(imageId);

        return ResponseEntity.noContent().build();
    }
}
