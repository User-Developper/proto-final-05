package com.medical.web_app.controller;



import com.medical.web_app.model.Patient;
import com.medical.web_app.service.WebAppService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping("/patients")
public class PatientViewController {

    private final WebAppService webAppService;

    public PatientViewController(WebAppService webAppService) {

        this.webAppService = webAppService;
    }

    @GetMapping("/search")
    public ModelAndView searchPage() {

        return new ModelAndView("patient-search");
    }

    @PostMapping("/search")
    public ModelAndView searchPatient(
            @RequestParam String email,
            Model model
    ) {

        Optional<Patient> patient =
                webAppService.getPatient(email);

        model.addAttribute("patient", patient.orElse(null));

        return new ModelAndView("patient-details");
    }
}