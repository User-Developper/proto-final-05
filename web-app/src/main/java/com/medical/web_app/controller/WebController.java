package com.medical.web_app.controller;

import com.medical.web_app.model.Patient;
import com.medical.web_app.model.User;
import com.medical.web_app.service.WebAppService;
import com.medical.web_app.service.WebAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/web")
public class WebController {




    @Autowired
    WebAppService webAppService;

    @GetMapping("/patient")
    public Optional<Patient> getPatient(@RequestParam String email){

        return webAppService.getPatient(email);
    }

    @GetMapping("/user")
    public Optional<User> getUser(@RequestParam String username){

        return webAppService.getUser(username);
    }





//    @GetMapping("/login")
//    public String login(){
//        return "login";
//    }
//
//
//    @GetMapping("/signup")
//    public String signup(Model model){
//        model.addAttribute("user", new User());
//        return "signup";
//    }
//
//
//    @PostMapping("/signup")
//    public String register(@ModelAttribute User user){
//        api.registerUser(user);
//        return "redirect:/login";
//    }
//
//
//    @GetMapping("/home")
//    public String home(){
//        return "home";
//    }
//
//
//    @GetMapping("/patients")
//    public String patients(Model model){
//        model.addAttribute("patients", api.getPatients());
//        model.addAttribute("patient", new Patient());
//        return "patients";
//    }
//
//
//    @PostMapping("/patients")
//    public String createPatient(@ModelAttribute Patient patient){
//        api.createPatient(patient);
//        return "redirect:/patients";
//    }
}