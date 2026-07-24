package org.example.controler;



import org.example.DTO.CreateReportRequest;
import org.example.DTO.ReportResponse;
import org.example.model.Report;
import org.example.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportController {



    @Autowired
    ReportRepository reportRepository;
//
//    @PostMapping()
//    public Report submitReportStep1(@RequestBody Report report){
//
//        return reportRepository.save(report);
//    }


    @PostMapping()
    public ReportResponse submitReportStep1(@RequestBody CreateReportRequest request) {

        Report report = new Report();

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



}
