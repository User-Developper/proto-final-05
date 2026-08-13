package com.medical.patient_service.service.form;


import com.medical.patient_service.model.*;
import com.medical.patient_service.service.DTO.CreateReportRequest;
import com.medical.patient_service.service.DTO.PageResponse;
import com.medical.patient_service.service.DTO.ReportResponse;
import com.medical.patient_service.service.DTO.ReportSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

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
    ReportResponse submitFinal(@RequestBody CreateReportRequest report);

    @GetMapping("/reports")
    List<Report> getReportsByUser(@RequestParam("userId") Long userId);




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


    @PostMapping("/client-selector/users/{username}/active-client/{clientId}")
    User setActiveClient(
            @PathVariable("username") String username,
            @PathVariable("clientId") Integer clientId
    );


}
