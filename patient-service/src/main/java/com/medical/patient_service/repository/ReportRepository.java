package com.medical.patient_service.repository;

import com.medical.patient_service.model.Category;
import com.medical.patient_service.model.Gravity;
import com.medical.patient_service.model.Report;
import com.medical.patient_service.model.User;
import com.medical.patient_service.service.DTO.ReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {
//    Page<Report> searchOnly(String q, Pageable pageable);
//
//    Page<Report> findByCategory(Category category, Pageable pageable);
//
//    Page<Report> findByGravity(Gravity gravity, Pageable pageable);
//
//    Page<Report> searchWithFilters(
//            String q,
//            Category category,
//            Gravity gravity,
//            Pageable pageable
//    );


//    Page<Report> findByCategory(Category category, Pageable pageable);
//
//    Page<Report> findByGravity(Gravity gravity, Pageable pageable);
//
//    Page<Report> findByCategoryAndGravity(Category category, Gravity gravity, Pageable pageable);
//
//    Page<Report> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndCategory(
//            String q, String q2, Category category, Pageable pageable);
//
//    Page<Report> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndGravity(
//            String q, String q2, Gravity gravity, Pageable pageable);
//
//    Page<Report> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndCategoryAndGravity(
//            String q, String q2, Category category, Gravity gravity, Pageable pageable);

}
