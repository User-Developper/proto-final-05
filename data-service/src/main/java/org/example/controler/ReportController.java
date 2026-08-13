package org.example.controler;



import org.example.DTO.CreateReportRequest;
import org.example.DTO.ReportResponse;
import org.example.DTO.ReportSummary;
import org.example.model.ProfessionalClient;
import org.example.model.Report;
import org.example.model.User;
import org.example.repository.ProfessionalClientRepository;
import org.example.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {



    @Autowired
    ReportRepository reportRepository;

    @Autowired
    ProfessionalClientRepository professionalClientRepository;
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



}
