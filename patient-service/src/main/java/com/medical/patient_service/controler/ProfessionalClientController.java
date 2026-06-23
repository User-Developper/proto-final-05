package com.medical.patient_service.controler;

import com.medical.patient_service.model.ProfessionalClient;
import com.medical.patient_service.model.User;
import com.medical.patient_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/client-selector")
public class ProfessionalClientController {

    @Autowired
    UserService userService;


    @GetMapping
    public List<ProfessionalClient> getClientsForUser(){


        return userService.getClientsForUser();
    }
}
