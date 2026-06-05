package com.medical.web_app.service;


import com.medical.web_app.model.Patient;
import com.medical.web_app.model.Report;
import com.medical.web_app.model.User;
import com.medical.web_app.service.dto.CreateReportRequest;
import com.medical.web_app.service.dto.ReportResponse;
import com.medical.web_app.service.form.ReportForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient("patient-service")
public interface PatientserviceFeignClient {


    @GetMapping("/patients")
    Optional<Patient> getPatient(@RequestParam("email") String email);


    @GetMapping("/user")
    Optional<User>  findUser(@RequestParam("username") String username);

    @PostMapping("/user")
    User addUser(@RequestBody User user);

    @PostMapping("/reports")
    ReportResponse submitReportStep1(@RequestBody CreateReportRequest request);


}
