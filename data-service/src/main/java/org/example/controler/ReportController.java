package org.example.controler;

import org.example.DTO.CreateReportImageRequest;
import org.example.DTO.CreateReportRequest;
import org.example.DTO.ReportImageResponse;
import org.example.DTO.ReportResponse;
import org.example.model.ProfessionalClient;
import org.example.model.Report;
import org.example.model.ReportImage;
import org.example.model.Status;
import org.example.repository.ProfessionalClientRepository;
import org.example.repository.ReportImageRepository;
import org.example.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    ProfessionalClientRepository professionalClientRepository;

    @Autowired
    ReportImageRepository reportImageRepository;


    // =========================================================
    // CLIENT
    // =========================================================

    @GetMapping("/client/{clientId}")
    public ProfessionalClient getClientById(
            @PathVariable Integer clientId) {

        return professionalClientRepository
                .findById(clientId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Client introuvable : " + clientId
                        )
                );
    }


    // =========================================================
    // CREATION REPORT
    // =========================================================

    @PostMapping()
    public ReportResponse submitReportStep1(
            @RequestBody CreateReportRequest request) {

        Report report = new Report();

        report.setUserId(request.getUserId());
        report.setClientId(request.getClientId());
        report.setTitle(request.getTitle());
        report.setDescription(request.getDescription());
        report.setCategory(request.getCategory());
        report.setGravity(request.getGravity());
        report.setLocation(request.getLocation());
        report.setLatitude(request.getLatitude());
        report.setLongitude(request.getLongitude());
        report.setDate(LocalDate.now());
        report.setStatus(Status.REPORTED);

        Report saved =
                reportRepository.save(report);

        ReportResponse response =
                new ReportResponse();

        response.setId(saved.getId());
        response.setUserId(saved.getUserId());
        response.setClientId(saved.getClientId());
        response.setTitle(saved.getTitle());
        response.setDescription(saved.getDescription());
        response.setCategory(saved.getCategory());
        response.setGravity(saved.getGravity());
        response.setLocation(saved.getLocation());
        response.setDate(saved.getDate());
        response.setLatitude(saved.getLatitude());
        response.setLongitude(saved.getLongitude());
        response.setStatus(saved.getStatus());

        return response;
    }


    // =========================================================
    // REPORTS D'UN UTILISATEUR
    // =========================================================

    @GetMapping()
    public List<Report> getReportsByUser(
            @RequestParam Long userId) {

        return reportRepository.findByUserId(userId);
    }


    // =========================================================
    // REPORT PAR ID
    // =========================================================

    @GetMapping("/{reportId}")
    public Report getReportById(
            @PathVariable Integer reportId) {

        return reportRepository.findById(reportId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Report introuvable : " + reportId
                        )
                );
    }


    // =========================================================
    // AJOUT IMAGE
    // =========================================================

    @PostMapping("/{reportId}/images")
    public ReportImageResponse addImage(
            @PathVariable Integer reportId,
            @RequestBody CreateReportImageRequest request) {

        Report report =
                reportRepository.findById(reportId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Report introuvable : "
                                                + reportId
                                )
                        );

        ReportImage image =
                new ReportImage();

        image.setReport(report);
        image.setFileName(
                request.getFileName()
        );
        image.setContentType(
                request.getContentType()
        );
        image.setStorageName(
                request.getStorageName()
        );
        image.setFileSize(
                request.getFileSize()
        );
        image.setStoragePath(
                request.getStoragePath()
        );

        if (request.getCreatedAt() != null) {

            image.setCreatedAt(
                    LocalDateTime.parse(
                            request.getCreatedAt()
                    )
            );

        } else {

            image.setCreatedAt(
                    LocalDateTime.now()
            );
        }

        ReportImage saved =
                reportImageRepository.save(image);

        return convertToResponse(saved);
    }


    // =========================================================
    // IMAGES D'UN REPORT
    // =========================================================

    @GetMapping("/{reportId}/images")
    public List<ReportImageResponse> getReportImages(
            @PathVariable Integer reportId) {

        return reportImageRepository
                .findByReportId(reportId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================================================
    // CONVERSION REPORT IMAGE -> DTO
    // =========================================================

    private ReportImageResponse convertToResponse(
            ReportImage image) {

        ReportImageResponse response =
                new ReportImageResponse();

        response.setId(
                image.getId()
        );

        if (image.getReport() != null) {

            response.setReportId(
                    image.getReport().getId()
            );
        }

        response.setFileName(
                image.getFileName()
        );

        response.setContentType(
                image.getContentType()
        );

        response.setStorageName(
                image.getStorageName()
        );

        response.setFileSize(
                image.getFileSize()
        );

        response.setStoragePath(
                image.getStoragePath()
        );

        response.setCreatedAt(
                image.getCreatedAt()
        );

        return response;
    }
}