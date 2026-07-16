package com.medical.patient_service.service.form;


import com.medical.patient_service.model.*;
import com.medical.patient_service.service.DTO.CreateReportRequest;
import com.medical.patient_service.service.DTO.PageResponse;
import com.medical.patient_service.service.DTO.ReportResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@FeignClient("data-service")
public interface DataServiceFeignClient {


    @GetMapping("/patients")
    Optional<Patient> getPatient(@RequestParam("email") String email);


    @GetMapping("/user")
    Optional<User>  findUser(@RequestParam("username") String username);

    @PostMapping("/user")
    User addUser(@RequestBody User user);

    @PostMapping("/reports")
    Report submitFinal(@RequestBody Report report);




    @GetMapping("/api/reports")
    PageResponse<ReportResponse> getAllReports(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Gravity gravity,
            @RequestParam(required = false) String q
    );

    @GetMapping("/client-selector")
    List<ProfessionalClient> getClientsForUser();




}
