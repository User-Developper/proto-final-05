package com.medical.web_app.service;


import com.medical.web_app.model.Category;
import com.medical.web_app.model.Gravity;
import com.medical.web_app.model.Patient;
import com.medical.web_app.model.User;
import com.medical.web_app.service.dto.CreateReportRequest;
import com.medical.web_app.service.dto.PageResponse;
import com.medical.web_app.service.dto.ReportResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
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
    CreateReportRequest submitFinal(@RequestBody CreateReportRequest request);

    @GetMapping("/reports")
    PageResponse<ReportResponse> getAllReports(@RequestParam("page") int page, @RequestParam("size") int size,@RequestParam(required = false) Category category,
                                               @RequestParam(required = false) Gravity gravity,
                                               @RequestParam(required = false) String q);


}
