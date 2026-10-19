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

    @PostMapping()
    public Report submitReportStep1(@RequestBody Report report){

        return reportRepository.save(report);
    }
0629383049
    0617537045


}
