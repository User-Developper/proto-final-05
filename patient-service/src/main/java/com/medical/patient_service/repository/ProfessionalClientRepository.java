package com.medical.patient_service.repository;


import com.medical.patient_service.model.ProfessionalClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessionalClientRepository extends JpaRepository<ProfessionalClient, Integer> {
}
