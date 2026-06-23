package org.example.controler;

import com.medical.patient_service.model.Category;
import com.medical.patient_service.model.Gravity;
import com.medical.patient_service.service.DTO.PageResponse;
import com.medical.patient_service.service.DTO.ReportResponse;
import com.medical.patient_service.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportApiController {

    @Autowired
    ReportService reportService;

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
