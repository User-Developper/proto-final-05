package org.example.repository;


import org.example.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ReportRepository extends JpaRepository<Report, Integer>, JpaSpecificationExecutor<Report> {

    List<Report> findByUserId(Long userId);


}
