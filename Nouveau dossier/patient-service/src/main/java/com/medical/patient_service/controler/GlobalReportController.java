package com.medical.patient_service.controler;

import com.medical.patient_service.model.Category;
import com.medical.patient_service.model.Gravity;
import com.medical.patient_service.service.DTO.PageResponse;
import com.medical.patient_service.service.DTO.ReportResponse;
import com.medical.patient_service.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class GlobalReportController {

    private final UserService userService;

    public GlobalReportController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public PageResponse<ReportResponse> getAllReports(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "6") int size,

            @RequestParam(required = false) Category category,

            @RequestParam(required = false) Gravity gravity,

            @RequestParam(required = false) String q) {

        return userService.getAllReports(
                page,
                size,
                category,
                gravity,
                q
        );
    }
}
