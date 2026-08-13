package com.medical.web_app.service;

import com.medical.web_app.model.*;
import com.medical.web_app.service.dto.*;
import com.medical.web_app.service.form.ReportForm;
import com.medical.web_app.service.form.SignupForm;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
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


    public User setActiveClient(String username, Integer clientId) {

        return patientserviceFeignClient.setActiveClient(username, clientId);
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



    public ReportResponse submitFinal(CreateReportTemp form) {

        System.out.println("USER ID AVANT ENVOI PATIENT SERVICE = "
                + form.getUserId());

        CreateReportRequest req = new CreateReportRequest();

        req.setTitle(form.getTitle());
        req.setDescription(form.getDescription());
        req.setCategory(form.getCategory());
        req.setGravity(form.getGravity());
        req.setLocation(form.getLocation());
        req.setLatitude(form.getLatitude());
        req.setLongitude(form.getLongitude());
        req.setUserId(form.getUserId());
        req.setClientId(form.getClientId());

        return patientserviceFeignClient.submitFinal(req);

    }



    public PageResponse<ReportResponse> getAllReports(int page, int size, Category category, Gravity gravity, String q) {

        return patientserviceFeignClient.getAllReports(page, size, category, gravity, q);

    }

    public List<ProfessionalClient> getClientsForUser(User user){


        return patientserviceFeignClient.getClientsForUser();


    }

    public List<ReportSummary> getReportsByUser(Long userId){

        return patientserviceFeignClient.getReportsByUser(userId);

    }

    public ProfessionalClient getClientById(Integer clientId){

        return patientserviceFeignClient.getClientById(clientId);

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