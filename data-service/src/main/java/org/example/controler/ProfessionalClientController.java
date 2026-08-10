package org.example.controler;


import org.example.model.ProfessionalClient;
import org.example.repository.ProfessionalClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/client-selector")
public class ProfessionalClientController {

  @Autowired
    ProfessionalClientRepository professionalClientRepository;


    @GetMapping
    public List<ProfessionalClient> getClientsForUser(){


        return professionalClientRepository.findAll();
    }
}
