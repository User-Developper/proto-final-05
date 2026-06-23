package com.medical.web_app.controller;


import com.medical.web_app.model.ProfessionalClient;
import com.medical.web_app.model.User;
import com.medical.web_app.service.WebAppService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class ProfessionalClientController {

    @Autowired
    WebAppService webAppService;


    @GetMapping("/client-selector")
    public String clientSelector(Model model, Principal principal, HttpSession httpSession){

        String username = principal.getName();

        User user = webAppService.getUser(username).orElse(null);

        List<ProfessionalClient> clients = webAppService.getClientsForUser(user);

        model.addAttribute("user", user);
        model.addAttribute("clients", clients);

        return "client-selector";

    }

    @GetMapping("/profile")
    public String userProfile(Model model, Principal principal, HttpSession httpSession){

        String username = principal.getName();

        User user = webAppService.getUser(username).orElse(null);
//
//        List<ProfessionalClient> clients = webAppService.getClientsForUser(user);
//
//        model.addAttribute("user", user);
//        model.addAttribute("clients", clients);

        return "profile";

    }
}
