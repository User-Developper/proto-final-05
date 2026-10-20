package org.example.repository;

import org.example.model.ReportImage;
import org.example.model.ReportImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportImageRepository extends JpaRepository<ReportImage, Integer> {

    List<ReportImage> findByReportId(Integer reportId);

}