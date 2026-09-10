
        package com.medical.patient_service.controler;

import com.medical.patient_service.model.Category;
import com.medical.patient_service.model.Gravity;
import com.medical.patient_service.model.ProfessionalClient;
import com.medical.patient_service.model.Report;
import com.medical.patient_service.service.DTO.CreateReportImageRequest;
import com.medical.patient_service.service.DTO.CreateReportRequest;
import com.medical.patient_service.service.DTO.PageResponse;
import com.medical.patient_service.service.DTO.ReportImageResponse;
import com.medical.patient_service.service.DTO.ReportResponse;
import com.medical.patient_service.service.DTO.ReportSummary;
import com.medical.patient_service.service.ImageStorageService;
import com.medical.patient_service.service.UserService;
import com.medical.patient_service.service.form.DataServiceFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    UserService userService;

    @Autowired
    DataServiceFeignClient dataServiceFeignClient;

    @Autowired
    private ImageStorageService imageStorageService;


    @PostMapping()
    public ReportResponse submitReportStep1(
            @RequestBody CreateReportRequest request) {

        return userService.submitReportStep1(request);
    }


    @GetMapping()
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


    @PostMapping(
            value = "/{reportId}/images",
            consumes = "multipart/form-data"
    )
    public List<ReportImageResponse> uploadImages(
            @PathVariable Integer reportId,
            @RequestPart("file") List<MultipartFile> files) {

        return files.stream()
                .map(file -> {

                    // 1. Enregistrer le fichier dans le storage
                    String storageName =
                            imageStorageService.store(file, reportId);

                    // 2. Préparer les informations pour data-service
                    CreateReportImageRequest request =
                            new CreateReportImageRequest();

                    request.setReportId(reportId);
                    request.setFileName(file.getOriginalFilename());
                    request.setContentType(file.getContentType());
                    request.setStorageName(storageName);
                    request.setFileSize(file.getSize());

                    request.setStoragePath(
                            imageStorageService.getStoragePath(
                                    reportId,
                                    storageName
                            )
                    );

                    request.setCreatedAt(
                            LocalDateTime.now().toString()
                    );

                    // 3. Enregistrer les informations dans la BDD
                    return dataServiceFeignClient.addReportImage(
                            reportId,
                            request
                    );
                })
                .toList();
    }
}

