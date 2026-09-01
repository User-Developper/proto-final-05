package com.medical.web_app.service;


import com.medical.web_app.model.*;
import com.medical.web_app.service.dto.CreateReportRequest;
import com.medical.web_app.service.dto.PageResponse;
import com.medical.web_app.service.dto.ReportResponse;
import com.medical.web_app.service.dto.ReportSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@FeignClient("patient-service")
public interface PatientserviceFeignClient {


    @GetMapping("/patients")
    Optional<Patient> getPatient(@RequestParam("email") String email);


    @GetMapping("/user")
    Optional<User>  findUser(@RequestParam("username") String username);

    @GetMapping("/profile")
    Optional<User> findUserByProfile(@RequestParam("username") String username);

    @GetMapping("/reports")
    List<ReportSummary> getReportsByUser(@RequestParam("userId") Long userId);


    @PostMapping("/user")
    User addUser(@RequestBody User user);

    @PostMapping("/reports")
    ReportResponse submitFinal(@RequestBody CreateReportRequest request);

//    @GetMapping("/reports")
//    PageResponse<ReportResponse> getAllReports(@RequestParam("page") int page, @RequestParam("size") int size,@RequestParam(required = false) Category category,
//                                               @RequestParam(required = false) Gravity gravity,
//                                               @RequestParam(required = false) String q);

    @GetMapping("/reports/client/{clientId}")
    ProfessionalClient getClientById(@PathVariable Integer clientId);

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

//    @GetMapping("/profile")
//    User user getUser();


//    @PostMapping(
//            value = "/reports/{reportId}/images",
//            consumes = "multipart/form-data"
//    )
//    void uploadReportImages(
//            @PathVariable("reportId") Integer reportId,
//            @RequestPart("images") List<MultipartFile> images
//    );

    @PostMapping(
            value = "/reports/{reportId}/images",
            consumes = "multipart/form-data"
    )
    String uploadReportImage(
            @PathVariable("reportId") Integer reportId,
            @RequestPart("file") MultipartFile file
    );


}


