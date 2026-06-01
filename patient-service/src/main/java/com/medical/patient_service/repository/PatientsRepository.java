package com.medical.patient_service.repository;

import com.medical.patient_service.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientsRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEmail(String email);

    void deleteByEmail(String email);






    }
