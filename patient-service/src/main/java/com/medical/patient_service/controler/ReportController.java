package com.medical.patient_service.controler;


import com.medical.patient_service.service.DTO.CreateReportRequest;
import com.medical.patient_service.service.DTO.ReportResponse;
import com.medical.patient_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    UserService userService;

    @PostMapping()
    public ReportResponse submitReportStep1(@RequestBody CreateReportRequest request){

        return userService.submitReportStep1(request);
    }

    @GetMapping()
    public List<ReportResponse> getAllReports(){

        return userService.getAllReports();
    }


}
