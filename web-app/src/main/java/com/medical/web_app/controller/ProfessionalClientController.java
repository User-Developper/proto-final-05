package com.medical.web_app.controller;


import com.medical.web_app.model.ProfessionalClient;
import com.medical.web_app.model.User;
import com.medical.web_app.service.WebAppService;
import com.medical.web_app.service.dto.ReportSummary;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

//    @PostMapping("/client-selector")
//    public String setActiveClient(@RequestParam Integer clientId, Principal principal, HttpSession httpSession) {
//
//        String username = principal.getName();
//
//        User user = webAppService
//                .getUser(username)
//                .orElseThrow(() ->
//                        new RuntimeException("Utilisateur introuvable")
//                );
//
//
//
//        webAppService.setActiveClient(user, client);
//
//    }

//    @PostMapping("/client-selector")
//    public String setActiveClient(
//            @RequestParam Integer clientId,
//            Principal principal) {
//
//        String username = principal.getName();
//
//        webAppService.setActiveClient(username, clientId);
//
//        return "redirect:/profile";
//    }

    @PostMapping("/client-selector")
    public String setActiveClient(
            @RequestParam Integer clientId,
            Principal principal,
            Model model) {

        String username = principal.getName();

        // Sauvegarde réelle du client actif
        User user = webAppService.setActiveClient(username, clientId);

        // Récupération du client qui vient d'être sélectionné
        ProfessionalClient client = user.getActiveClient();

        model.addAttribute("user", user);
        model.addAttribute("selectedClient", client);

        // On indique à la page que la sauvegarde a réussi
        model.addAttribute("clientSelected", true);

        // On recharge les clients pour conserver la carte
        List<ProfessionalClient> clients =
                webAppService.getClientsForUser(user);

        model.addAttribute("clients", clients);

        return "client-selector";
    }


    @GetMapping("/profile")
    public String userProfile(Model model, Principal principal, HttpSession httpSession){

        String username = principal.getName();

        User user = webAppService.getUser(username).orElse(null);
//
        List<ProfessionalClient> clients = webAppService.getClientsForUser(user);

        assert user != null;
        Long userId = user.getId();

        List<ReportSummary> reports = webAppService.getReportsByUser(userId);
//
        model.addAttribute("user", user);
//        model.addAttribute("clients", clients);

        System.out.println("USER ID = " + userId);
        System.out.println("REPORTS = " + reports);

        model.addAttribute("user", user);
        model.addAttribute("reports", reports);

        return "profile";

    }

//    @GetMapping("/user/{id}")
//    public List<ReportSummary> getReportsByUser(@PathVariable Integer Id){
//
//
//
//    }
}
