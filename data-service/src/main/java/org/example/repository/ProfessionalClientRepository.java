package org.example.repository;


import org.example.model.ProfessionalClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessionalClientRepository extends JpaRepository<ProfessionalClient, Integer> {
}
