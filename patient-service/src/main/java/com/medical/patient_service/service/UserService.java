package com.medical.patient_service.service;


//import com.medical.patient_service.model.ProfessionalClient;
import com.medical.patient_service.model.ProfessionalClient;
import com.medical.patient_service.model.Report;
import com.medical.patient_service.model.User;
//import com.medical.patient_service.repository.ProfessionalClientRepository;
//import com.medical.patient_service.repository_del.ProfessionalClientRepository;
//import com.medical.patient_service.repository_del.ReportRepository;
//import com.medical.patient_service.repository_del.UserRepository;
import com.medical.patient_service.service.DTO.CreateReportRequest;
import com.medical.patient_service.service.DTO.ReportResponse;
import com.medical.patient_service.service.DTO.ReportSummary;
import com.medical.patient_service.service.form.DataServiceFeignClient;
//import org.example.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {





    @Autowired
    DataServiceFeignClient dataServiceFeignClient;



    public User addUser(User user){

        return dataServiceFeignClient.addUser(user);
    }

    public Optional<User> findUser(String username){

        return dataServiceFeignClient.findUser(username);
    }

//    public User updateUser(User user){
//
//        return dataServiceFeignClient.save(user);
//    }
//
//    public void deleteUser(String username){
//
//        dataServiceFeignClient.deleteByUsername(username);
//    }


//    public ReportResponse submitReportStep1(CreateReportRequest request) {
//
//        Report report = new Report();
//
//        report.setTitle(request.getTitle());
//        report.setDescription(request.getDescription());
//        report.setCategory(request.getCategory());
//        report.setGravity(request.getGravity());
//        report.setLocation(request.getLocation());
//        report.setLatitude(request.getLatitude());
//        report.setLongitude(request.getLongitude());
//
//        Report saved = dataServiceFeignClient.submitFinal(report);
//
//        ReportResponse response = new ReportResponse();
//
//        response.setId(saved.getId());
//        response.setTitle(saved.getTitle());
//        response.setDescription(saved.getDescription());
//        response.setCategory(saved.getCategory());
//        response.setGravity(saved.getGravity());
//        response.setLocation(saved.getLocation());
//        response.setLatitude(saved.getLatitude());
//        response.setLongitude(saved.getLongitude());
//
//        return response;
//    }


    public ReportResponse submitReportStep1(CreateReportRequest request) {

        // logique métier
        // validations
        // enrichissement éventuel

//        return dataServiceFeignClient.submitFinal(request);


        System.out.println("AVANT APPEL DATA-SERVICE");

        ReportResponse response = dataServiceFeignClient.submitFinal(request);

        System.out.println("REPONSE DATA-SERVICE : " + response);

        return response;
    }

    public List<ReportSummary> getReportsByUser(Long userId){

        List<Report> reports = dataServiceFeignClient.getReportsByUser(userId);

        return reports.stream()
                .map(this::toSummary)
                .toList();

    }

    private ReportSummary toSummary(Report report){

        ReportSummary dto = new ReportSummary();

        dto.setId(report.getId());
        dto.setUserId(report.getUserId());
        dto.setTitle(report.getTitle());
        dto.setDescription(report.getDescription());
        dto.setLocation(report.getLocation());
        dto.setLatitude(report.getLatitude());
        dto.setLongitude(report.getLongitude());
        dto.setCategory(report.getCategory());
        dto.setGravity(report.getGravity());

        return dto;
    }

//    public List<ReportSummary> getReportsByUser(ReportSummary reportSummary) {
//
//
//
//        Report report = new Report();
//
//        report.setTitle(reportSummary.getTitle());
//        report.setDescription(reportSummary.getDescription());
//        report.setCategory(reportSummary.getCategory());
//        report.setGravity(reportSummary.getGravity());
//        report.setLocation(reportSummary.getLocation());
//        report.setLatitude(reportSummary.getLatitude());
//        report.setLongitude(reportSummary.getLongitude());
//
//        Report saved = reportRepository.save(report);
//
//
//
//    }


//    @Autowired
//    ProfessionalClientRepository professionalClientRepository;


    public List<ProfessionalClient> getClientsForUser(){

        return dataServiceFeignClient.getClientsForUser();

    }



//    public List<ReportResponse> getAllReports() {
//
//        return reportRepository.findAll()
//                .stream()
//                .map(this::toResponse)
//                .toList();
//    }
//
//    public ReportResponse toResponse(Report report) {
//
//        ReportResponse response = new ReportResponse();
//
//        response.setId(report.getId());
//        response.setTitle(report.getTitle());
//        response.setDescription(report.getDescription());
//        response.setCategory(report.getCategory());
//        response.setGravity(report.getGravity());
//        response.setLatitude(report.getLatitude());
//        response.setLongitude(report.getLongitude());
//        response.setLocation(report.getLocation());
//
//        return response;
//    }

//    public ReportResponse getAllReports(){
//
//        ReportResponse reponse = new ReportResponse();
//
//
//
//
//
//    }





//    public ReportResponse createReport(CreateReportRequest request) {
//
//        Report report = new Report();
//
//        report.setTitle(request.getTitle());
//        report.setDescription(request.getDescription());
//        report.setCategory(request.getCategory());
//        report.setGravity(request.getGravity());
//        report.setLatitude(request.getLatitude());
//        report.setLongitude(request.getLongitude());
//        report.setLocation(request.getLocation());
//
//        Report saved = userRepository.save(report);
//
//        return mapToResponse(saved);
//    }



//    public  Report submitReportStep1(CreateReportRequest createReportRequest){
//
//
//        Report report = new Report();
//
//        report.setCategory(createReportRequest.getCategory());
//        report.setTitle(createReportRequest.getTitle());
//
//
//
//    }
}
