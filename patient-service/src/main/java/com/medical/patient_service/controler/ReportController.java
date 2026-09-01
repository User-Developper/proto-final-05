package com.medical.patient_service.controler;


import com.medical.patient_service.model.Category;
import com.medical.patient_service.model.Gravity;
import com.medical.patient_service.model.ProfessionalClient;
import com.medical.patient_service.service.DTO.CreateReportRequest;
import com.medical.patient_service.service.DTO.PageResponse;
import com.medical.patient_service.service.DTO.ReportResponse;
//import com.medical.patient_service.service.ReportService;
import com.medical.patient_service.service.DTO.ReportSummary;
import com.medical.patient_service.service.ImageStorageService;
import com.medical.patient_service.service.UserService;
import com.medical.patient_service.service.form.DataServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    UserService userService;
//
//    @Autowired
//    ReportService reportService;

    @Autowired
    DataServiceFeignClient dataServiceFeignClient;

    @Autowired
    private ImageStorageService imageStorageService;

    @PostMapping()
    public ReportResponse submitReportStep1(@RequestBody CreateReportRequest request){

        return userService.submitReportStep1(request);
    }

    @GetMapping()
    public List<ReportSummary> getReportsByUser(@RequestParam Long userId){

        return userService.getReportsByUser(userId);

    }

    @GetMapping("/client/{clientId}")
    public ProfessionalClient getClientById(@PathVariable Integer clientId){

        return  userService.getClientById(clientId);

    }

    @PostMapping(
            value = "/{reportId}/images",
            consumes = "multipart/form-data"
    )
    public List<String> uploadImages(
            @PathVariable Integer reportId,
            @RequestPart("file") List<MultipartFile> files) {

        return files.stream()
                .map(file -> imageStorageService.store(file, reportId))
                .toList();
    }

//    @PostMapping("/{reportId}/images")
//    public String uploadImage(
//            @PathVariable Integer reportId,
//            @RequestParam("file") MultipartFile file) {
//
//        String storageName = imageStorageService.store(file, reportId);
//
//        return storageName;
//    }






//    @GetMapping()
//    public PageResponse<ReportResponse> getAllReports(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size, @RequestParam(required = false)Category category, @RequestParam(required = false)Gravity gravity, @RequestParam(required = false) String q){
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
//
//        return reportService.getAllReports(pageable, category, gravity, q);
//    }

//    @GetMapping("/api/reports")
//    public PageResponse<ReportResponse> getReports(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "6") int size,
//            @RequestParam(required = false) Category category,
//            @RequestParam(required = false) Gravity gravity,
//            @RequestParam(required = false) String q
//    ) {
//        return reportService.getAllReports(page, size, category, gravity, q);
//    }









}
