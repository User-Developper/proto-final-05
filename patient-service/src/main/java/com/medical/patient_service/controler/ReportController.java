
package com.medical.patient_service.controler;

import com.medical.patient_service.model.ProfessionalClient;
import com.medical.patient_service.model.Report;
import com.medical.patient_service.service.DTO.CreateReportRequest;
import com.medical.patient_service.service.DTO.ReportImageResponse;
import com.medical.patient_service.service.DTO.ReportImageStorageResponse;
import com.medical.patient_service.service.DTO.ReportResponse;
import com.medical.patient_service.service.DTO.ReportSummary;
import com.medical.patient_service.service.DTO.PageResponse;
import com.medical.patient_service.service.UserService;
import com.medical.patient_service.service.form.DataServiceFeignClient;
import com.medical.patient_service.service.form.ReportStorageFeignClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private UserService userService;

    @Autowired
    private DataServiceFeignClient dataServiceFeignClient;

    @Autowired
    private ReportStorageFeignClient reportStorageFeignClient;

    @PostMapping
    public ReportResponse submitReportStep1(
            @RequestBody CreateReportRequest request) {
        return userService.submitReportStep1(request);
    }

    @GetMapping
    public List<ReportSummary> getReportsByUser(
            @RequestParam Long userId) {
        return userService.getReportsByUser(userId);
    }

    @GetMapping("/{reportId}")
    public Report getReportById(
            @PathVariable Integer reportId) {
        return userService.getReportById(reportId);
    }

    @GetMapping("/client/{clientId}")
    public ProfessionalClient getClientById(
            @PathVariable Integer clientId) {
        return userService.getClientById(clientId);
    }

    @GetMapping("/{reportId}/images")
    public List<ReportImageStorageResponse> getReportImages(
            @PathVariable Integer reportId) {
        return reportStorageFeignClient.getImages(reportId);
    }

    @PostMapping(
            value = "/{reportId}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public List<ReportImageStorageResponse> uploadImages(
            @PathVariable Integer reportId,
            @RequestPart("file") List<MultipartFile> files) {

        return files.stream()
                .map(file ->
                        reportStorageFeignClient.uploadImage(
                                reportId, file
                        )
                )
                .toList();
    }

    @GetMapping("/{reportId}/images/{imageId}")
    public ResponseEntity<Resource> getReportImage(
            @PathVariable Integer reportId,
            @PathVariable String imageId) {

        // Vérifie que l'image appartient bien au signalement demandé.
        List<ReportImageStorageResponse> images =
                reportStorageFeignClient.getImages(reportId);

        boolean belongsToReport = images.stream()
                .anyMatch(image -> imageId.equals(image.getId()));

        if (!belongsToReport) {
            return ResponseEntity.notFound().build();
        }

        Resource resource =
                reportStorageFeignClient.getImage(imageId);

        String filename = resource.getFilename();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                                (filename != null ? filename : "image") +
                                "\""
                )
                .body(resource);
    }

    @DeleteMapping("/{reportId}/images/{imageId}")
    public ResponseEntity<Void> deleteReportImage(
            @PathVariable Integer reportId,
            @PathVariable String imageId) {

        List<ReportImageStorageResponse> images =
                reportStorageFeignClient.getImages(reportId);

        boolean belongsToReport = images.stream()
                .anyMatch(image -> imageId.equals(image.getId()));

        if (!belongsToReport) {
            return ResponseEntity.notFound().build();
        }

        reportStorageFeignClient.deleteImage(imageId);

        return ResponseEntity.noContent().build();
    }
}
