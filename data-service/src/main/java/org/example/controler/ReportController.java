package org.example.controler;



import org.example.DTO.CreateReportImageRequest;
import org.example.DTO.CreateReportRequest;
import org.example.DTO.ReportResponse;
import org.example.DTO.ReportSummary;
import org.example.model.ProfessionalClient;
import org.example.model.Report;
import org.example.model.ReportImage;
import org.example.model.User;
import org.example.repository.ProfessionalClientRepository;
import org.example.repository.ReportImageRepository;
import org.example.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.DTO.CreateReportImageRequest;
import org.example.model.ReportImage;

import java.security.Principal;
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
//
//    @PostMapping()
//    public Report submitReportStep1(@RequestBody Report report){
//
//        return reportRepository.save(report);
//    }

    @GetMapping("/client/{clientId}")
    public ProfessionalClient getClientById(@PathVariable Integer clientId){


//        return  professionalClientRepository.findById(clientId);

        return professionalClientRepository
                .findById(clientId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Client introuvable : " + clientId
                        )
                );


    }


    @PostMapping()
    public ReportResponse submitReportStep1(@RequestBody CreateReportRequest request) {

        Report report = new Report();

//        User user = new User();

        report.setUserId(request.getUserId());
        report.setClientId(request.getClientId());
//        report.setUserId(user.getId());
        report.setTitle(request.getTitle());
        report.setDescription(request.getDescription());
        report.setCategory(request.getCategory());
        report.setGravity(request.getGravity());
        report.setLocation(request.getLocation());
        report.setLatitude(request.getLatitude());
        report.setLongitude(request.getLongitude());

        Report saved = reportRepository.save(report);

        ReportResponse response = new ReportResponse();
        response.setId(saved.getId());
        response.setTitle(saved.getTitle());
        response.setDescription(saved.getDescription());
        response.setCategory(saved.getCategory());
        response.setGravity(saved.getGravity());
        response.setLocation(saved.getLocation());
        response.setLatitude(saved.getLatitude());
        response.setLongitude(saved.getLongitude());

        return response;
    }

    @GetMapping()
    public List<Report> getReportsByUser(@RequestParam Long userId){

        return reportRepository.findByUserId(userId);
    }


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


    @PostMapping("/{reportId}/images")
    public ReportImage addImage(
            @PathVariable Integer reportId,
            @RequestBody CreateReportImageRequest request) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Report introuvable : " + reportId
                        )
                );

        ReportImage image = new ReportImage();

        image.setReport(report);
        image.setFileName(request.getFileName());
        image.setContentType(request.getContentType());
        image.setStorageName(request.getStorageName());
        image.setFileSize(request.getFileSize());
        image.setStoragePath(request.getStoragePath());

        if (request.getCreatedAt() != null) {
            image.setCreatedAt(
                    java.time.LocalDateTime.parse(request.getCreatedAt())
            );
        } else {
            image.setCreatedAt(java.time.LocalDateTime.now());
        }

        return reportImageRepository.save(image);
    }


}
