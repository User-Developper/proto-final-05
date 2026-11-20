//package com.medical.patient_service.controler;
//
//
//import com.medical.patient_service.model.Patient;
//import com.medical.patient_service.service.PatientsService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/patients")
//public class PatientsController {
//
//    @Autowired
//    PatientsService patientsService;
//
//
//
//    @PostMapping()
//    public Patient createPatient(@RequestBody Patient patient){
//
//       return patientsService.createPatient(patient);
//
//    }
//
//    @GetMapping()
//    public Optional<Patient> getPatient(@RequestParam ("firstname") String firstname, @RequestParam ("email") String email){
//
//        return patientsService.findPatient(email);
//
//    }
//
//    @PutMapping()
//    public Patient updatePatient(@RequestBody Patient patient){
//
//        return patientsService.updatePatient(patient);
//    }
//
//    @DeleteMapping()
//    public void deletaPerson(@RequestParam String email){
//
//        patientsService.deletePatient(email);
//    }
//
//
//}
