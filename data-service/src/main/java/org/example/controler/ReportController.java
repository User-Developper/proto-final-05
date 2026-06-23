package org.example.controler;


import com.medical.patient_service.service.DTO.CreateReportRequest;
import com.medical.patient_service.service.DTO.ReportResponse;
import com.medical.patient_service.service.ReportService;
import com.medical.patient_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    UserService userService;

    @Autowired
    ReportService reportService;

    @PostMapping()
    public ReportResponse submitReportStep1(@RequestBody CreateReportRequest request){

        return userService.submitReportStep1(request);
    }

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
