package com.medical.patient_service.service;


import com.medical.patient_service.model.Report;
import com.medical.patient_service.model.User;
import com.medical.patient_service.repository.ReportRepository;
import com.medical.patient_service.repository.UserRepository;
import com.medical.patient_service.service.DTO.CreateReportRequest;
import com.medical.patient_service.service.DTO.ReportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {



    @Autowired
    UserRepository userRepository;

    @Autowired
    ReportRepository reportRepository;



    public User addUser(User user){

        return userRepository.save(user);
    }

    public Optional<User> findUser(String username){

        return userRepository.findByUsername(username);
    }

    public User updateUser(User user){

        return userRepository.save(user);
    }

    public void deleteUser(String username){

        userRepository.deleteByUsername(username);
    }


    public ReportResponse submitReportStep1(CreateReportRequest request) {

        Report report = new Report();

        report.setTitle(request.getTitle());
        report.setDescription(request.getDescription());
        report.setCategory(request.getCategory());
        report.setGravity(request.getGravity());
        report.setLocation(request.getLocation());
        report.setLatitude(request.getLatitude());
        report.setLongitude(request.getLongitude());

        Report saved = reportRepository.save(report);

        ReportResponse response = new ReportResponse();

        response.setId(saved.getId());
        response.setTitle(saved.getTitle());
        response.setDescription(saved.getDescription());
        response.setCategory(saved.getCategory());
        response.setGravity(saved.getGravity());
        response.setLocation(saved.getLocation());
        response.setLatitude(saved.getLatitude());
        response.setLongitude(saved.getLongitude());

        return response;
    }



    public List<ReportResponse> getAllReports() {

        return reportRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ReportResponse toResponse(Report report) {

        ReportResponse response = new ReportResponse();

        response.setId(report.getId());
        response.setTitle(report.getTitle());
        response.setDescription(report.getDescription());
        response.setCategory(report.getCategory());
        response.setGravity(report.getGravity());
        response.setLatitude(report.getLatitude());
        response.setLongitude(report.getLongitude());
        response.setLocation(report.getLocation());

        return response;
    }

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
