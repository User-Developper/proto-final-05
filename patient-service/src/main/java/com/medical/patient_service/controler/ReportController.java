package com.medical.patient_service.controler;


import com.medical.patient_service.model.Category;
import com.medical.patient_service.model.Gravity;
import com.medical.patient_service.service.DTO.CreateReportRequest;
import com.medical.patient_service.service.DTO.PageResponse;
import com.medical.patient_service.service.DTO.ReportResponse;
import com.medical.patient_service.service.ReportService;
import com.medical.patient_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    UserService userService;

    @Autowired
    ReportService reportService;

    @PostMapping()
    public ReportResponse submitReportStep1(@RequestBody CreateReportRequest request){

        return userService.submitReportStep1(request);
    }

    @GetMapping()
    public PageResponse<ReportResponse> getAllReports(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size, @RequestParam(required = false)Category category, @RequestParam(required = false)Gravity gravity, @RequestParam(required = false) String q){

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        return reportService.getAllReports(pageable, category, gravity, q);
    }

    @GetMapping()
    public PageResponse<ReportResponse> getReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Gravity gravity,
            @RequestParam(required = false) String q
    ) {
        return reportService.getAllReports(page, size, category, gravity, q);
    }







}
