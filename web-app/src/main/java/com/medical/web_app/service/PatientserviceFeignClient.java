package com.medical.web_app.service;

import com.medical.web_app.model.*;
import com.medical.web_app.service.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@FeignClient("patient-service")
public interface PatientserviceFeignClient {

    @GetMapping("/patients")
    Optional<Patient> getPatient(
            @RequestParam("email") String email
    );


    @GetMapping("/user")
    Optional<User> findUser(
            @RequestParam("username") String username
    );


    @GetMapping("/profile")
    Optional<User> findUserByProfile(
            @RequestParam("username") String username
    );


    @GetMapping("/reports")
    List<ReportSummary> getReportsByUser(
            @RequestParam("userId") Long userId
    );


    @GetMapping("/reports/{reportId}")
    ReportResponse getReportById(
            @PathVariable("reportId") Integer reportId
    );


    @GetMapping("/user/{userId}")
    User getUserById(
            @PathVariable("userId") Long userId
    );


    @GetMapping("/reports/{reportId}/images")
    List<ReportImageResponse> getReportImages(
            @PathVariable("reportId") Integer reportId
    );


    @PostMapping("/user")
    User addUser(
            @RequestBody User user
    );


    @PostMapping("/reports")
    ReportResponse submitFinal(
            @RequestBody CreateReportRequest request
    );


    @GetMapping("/reports/client/{clientId}")
    ProfessionalClient getClientById(
            @PathVariable("clientId") Integer clientId
    );


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


    @PostMapping(
            "/client-selector/users/{username}/active-client/{clientId}"
    )
    User setActiveClient(
            @PathVariable("username") String username,
            @PathVariable("clientId") Integer clientId
    );


    @PostMapping(
            value = "/reports/{reportId}/images",
            consumes = "multipart/form-data"
    )
    String uploadReportImage(
            @PathVariable("reportId") Integer reportId,
            @RequestPart("file") MultipartFile file
    );

    @GetMapping("/reports/{reportId}/images/{storageName}")
    byte[] getReportImage(
            @PathVariable("reportId") Integer reportId,
            @PathVariable("storageName") String storageName
    );
}