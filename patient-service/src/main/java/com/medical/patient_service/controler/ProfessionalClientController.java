package com.medical.patient_service.controler;

import com.medical.patient_service.model.ProfessionalClient;
import com.medical.patient_service.model.User;
import com.medical.patient_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    @PostMapping("/users/{username}/active-client/{clientId}")
    public User setActiveClient(
            @PathVariable String username,
            @PathVariable Integer clientId) {



        return userService.setActiveClient(username, clientId);


    }
}
