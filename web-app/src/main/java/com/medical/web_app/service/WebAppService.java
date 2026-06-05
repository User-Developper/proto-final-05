package com.medical.web_app.service;

import com.medical.web_app.model.Patient;
import com.medical.web_app.model.Report;
import com.medical.web_app.model.User;
import com.medical.web_app.service.dto.CreateReportRequest;
import com.medical.web_app.service.dto.ReportResponse;
import com.medical.web_app.service.form.ReportForm;
import com.medical.web_app.service.form.SignupForm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebAppService {

    private final PatientserviceFeignClient patientserviceFeignClient;
    private final PasswordEncoder passwordEncoder;

    public WebAppService(PatientserviceFeignClient patientserviceFeignClient, PasswordEncoder passwordEncoder) {
        this.patientserviceFeignClient = patientserviceFeignClient;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Patient> getPatient(String email) {
        return patientserviceFeignClient.getPatient(email);
    }

    public Optional<User> getUser(String username) {
        return patientserviceFeignClient.findUser(username);
    }

    public User getUserByUsername(String username) {
        return getUser(username).orElse(null);
    }


    public User createUser(SignupForm form) {

        User user = new User();

        user.setUsername(form.getUsername());
        user.setPrenom(form.getPrenom());
        user.setEmail(form.getEmail());
        user.setRole("PATIENT");

        user.setPassword(passwordEncoder.encode(form.getPassword()));

        return patientserviceFeignClient.addUser(user);
    }



    public ReportResponse submitReportStep1(ReportForm form) {

        CreateReportRequest req = new CreateReportRequest();

        req.setTitle(form.getTitle());
        req.setDescription(form.getDescription());
        req.setCategory(form.getCategory());
        req.setGravity(form.getGravity());
        req.setLocation(form.getLocation());
        req.setLatitude(form.getLatitude());
        req.setLongitude(form.getLongitude());

        return patientserviceFeignClient.submitReportStep1(req);
    }




//    public ReportResponse submitReportStep1(ReportForm form) {
//
//        CreateReportRequest report = new CreateReportRequest();
//
//        report.setTitle(form.getTitle());
//        report.setDescription(form.getDescription());
//        report.setCategory(form.getCategory());
//        report.setGravity(form.getGravity());
//        report.setLocation(form.getLocation());
//        report.setLatitude(form.getLatitude());
//        report.setLongitude(form.getLongitude());
//
//        return patientserviceFeignClient.submitReportStep1(report);
    }
























//package com.medical.web_app.service;
//
//import com.medical.web_app.model.Patient;
//import com.medical.web_app.model.User;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class WebAppService {
//
//
//
//
//    @Autowired
//    PatientserviceFeignClient patientserviceFeignClient;
//
//
//
//        public Optional<Patient> getPatient(String email){
//
//            return patientserviceFeignClient.getPatientDetails(email);
//
//    }
//
//
//    public Optional<User>  getUser(String username) {
//
//            return patientserviceFeignClient.getUserDetails(username);
//    }
//
//    public User createUser(User user) {
//        return patientserviceFeignClient.createUser(user);
//    }
//
//    public User getUserByUsername(String username) {
//        return patientserviceFeignClient.getUserDetails(username)
//                .orElse(null);
//    }
//}