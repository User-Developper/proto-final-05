package org.example.controler;


import org.example.model.ProfessionalClient;
import org.example.model.User;
import org.example.repository.ProfessionalClientRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/client-selector")
public class ProfessionalClientController {

  @Autowired
    ProfessionalClientRepository professionalClientRepository;

  @Autowired
  UserRepository userRepository;


    @GetMapping
    public List<ProfessionalClient> getClientsForUser(){


        return professionalClientRepository.findAll();
    }

  @PostMapping("/users/{username}/active-client/{clientId}")
  public User setActiveClient(
          @PathVariable String username,
          @PathVariable Integer clientId) {





    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

    ProfessionalClient client = professionalClientRepository.findById(clientId)
            .orElseThrow(() -> new RuntimeException("Client introuvable"));

    if (!user.getClients().contains(client)) {
      user.getClients().add(client);
    }

    user.setActiveClient(client);

    return userRepository.save(user);


  }
}
