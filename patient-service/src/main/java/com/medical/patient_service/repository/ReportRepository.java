package com.medical.patient_service.repository;

import com.medical.patient_service.model.Report;
import com.medical.patient_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface ReportRepository extends JpaRepository<Report, Long> {



}
