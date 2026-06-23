package org.example.service;


import com.medical.patient_service.model.Patient;
import com.medical.patient_service.repository.PatientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PatientsService {

    @Autowired
    PatientsRepository patientsRepository;





    public Patient createPatient(Patient patient){

       return patientsRepository.save(patient);

    }

    public Optional<Patient> findPatient(String email){

        return  patientsRepository.findByEmail(email);

    }

    public Patient updatePatient(Patient patient){

        return patientsRepository.save(patient);
    }

    public void deletePatient(String email){

         patientsRepository.deleteByEmail(email);
    }


}
