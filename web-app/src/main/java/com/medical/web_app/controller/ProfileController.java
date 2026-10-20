//package com.medical.web_app.controller;
//
//
//import com.medical.web_app.model.User;
//import com.medical.web_app.service.WebAppService;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.security.Principal;
//
//@Controller
//@RequestMapping("/profile")
//public class ProfileController {
//
//    private final WebAppService webAppService;
//
//
//    public ProfileController(WebAppService webAppService) {
//        this.webAppService = webAppService;
//
//    }
//
//
//    @GetMapping()
//    public String profilePage(Model model, Principal principal) {
//
//        String username = principal.getName();
//
//        User user = webAppService.getUser(username)
//                .orElse(null);
//
//        model.addAttribute("user", user);
//
//        return "profile";
//
//
//    }
//}
