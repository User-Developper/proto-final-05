
package org.example.controler;

import org.example.DTO.PageResponse;
import org.example.DTO.ReportResponse;
import org.example.model.Category;
import org.example.model.Gravity;
import org.example.model.Report;
import org.example.repository.ReportRepository;
import org.example.repository.ReportSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class GlobalReportController {

    private final ReportRepository reportRepository;

    public GlobalReportController(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @GetMapping
    public PageResponse<ReportResponse> getAllReports(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "6") int size,

            @RequestParam(required = false) Category category,

            @RequestParam(required = false) Gravity gravity,

            @RequestParam(required = false) String q) {

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException(
                    "Les paramètres de pagination sont invalides."
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.DESC,
                        "date",
                        "id"
                )
        );

        Page<Report> reports = reportRepository.findAll(
                ReportSpecification.withFilters(
                        category,
                        gravity,
                        q
                ),
                pageable
        );

        PageResponse<ReportResponse> response =
                new PageResponse<>();

        response.setContent(
                reports.getContent()
                        .stream()
                        .map(this::convertToResponse)
                        .toList()
        );

        response.setNumber(reports.getNumber());
        response.setSize(reports.getSize());
        response.setTotalElements(reports.getTotalElements());
        response.setTotalPages(reports.getTotalPages());

        return response;
    }

    private ReportResponse convertToResponse(Report report) {

        ReportResponse response = new ReportResponse();

        response.setId(report.getId());
        response.setUserId(report.getUserId());
        response.setClientId(report.getClientId());
        response.setTitle(report.getTitle());
        response.setDescription(report.getDescription());
        response.setCategory(report.getCategory());
        response.setGravity(report.getGravity());
        response.setLocation(report.getLocation());
        response.setDate(report.getDate());
        response.setLatitude(report.getLatitude());
        response.setLongitude(report.getLongitude());
        response.setStatus(report.getStatus());

        return response;
    }
}
