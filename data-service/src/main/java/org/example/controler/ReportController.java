package org.example.controler;



import org.example.DTO.CreateReportRequest;
import org.example.DTO.ReportResponse;
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

    @PostMapping()
    public ReportResponse submitReportStep1(@RequestBody CreateReportRequest request){

        return reportRepository.save(report);
    }



}
