package com.medical.web_app.controller;

import com.medical.web_app.model.Category;
import com.medical.web_app.model.Gravity;
import com.medical.web_app.service.WebAppService;
import com.medical.web_app.service.dto.PageResponse;
import com.medical.web_app.service.dto.ReportResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ReportApiController {

    private final WebAppService webAppService;

    public ReportApiController(WebAppService webAppService) {
        this.webAppService = webAppService;
    }

    @GetMapping("/reports")
    public PageResponse<ReportResponse> getReports( @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "6") int size,
                                                    @RequestParam(required = false) Category category,
                                                    @RequestParam(required = false) Gravity gravity,
                                                    @RequestParam(required = false) String q) {
        return webAppService.getAllReports(page, size, category, gravity, q);
    }
}
