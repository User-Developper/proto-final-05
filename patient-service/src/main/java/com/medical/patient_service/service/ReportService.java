package com.medical.patient_service.service;

import com.medical.patient_service.model.Category;
import com.medical.patient_service.model.Gravity;
import com.medical.patient_service.model.Report;
import com.medical.patient_service.repository.ReportRepository;
import com.medical.patient_service.service.DTO.PageResponse;
import com.medical.patient_service.service.DTO.ReportResponse;
import com.medical.patient_service.specification.ReportSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public PageResponse<ReportResponse> getAllReports(
            int page,
            int size,
            Category category,
            Gravity gravity,
            String q
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Specification<Report> spec = Specification
                .where(ReportSpecification.containsText(q))
                .and(ReportSpecification.hasCategory(category))
                .and(ReportSpecification.hasGravity(gravity));

        Page<Report> result = reportRepository.findAll(spec, pageable);

        return mapToResponse(result);
    }

    private PageResponse<ReportResponse> mapToResponse(Page<Report> result) {

        PageResponse<ReportResponse> res = new PageResponse<>();

        res.setContent(
                result.getContent().stream().map(r -> {
                    ReportResponse dto = new ReportResponse();
                    dto.setId(r.getId());
                    dto.setTitle(r.getTitle());
                    dto.setDescription(r.getDescription());
                    dto.setCategory(r.getCategory());
                    dto.setGravity(r.getGravity());
                    return dto;
                }).toList()
        );

        res.setNumber(result.getNumber());
        res.setSize(result.getSize());
        res.setTotalElements(result.getTotalElements());
        res.setTotalPages(result.getTotalPages());

        return res;
    }
}