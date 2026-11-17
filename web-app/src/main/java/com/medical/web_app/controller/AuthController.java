package com.medical.web_app.controller;

import com.medical.web_app.model.User;
import com.medical.web_app.service.WebAppService;
import com.medical.web_app.service.form.SignupForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthController {

    private final WebAppService webAppService;


    public AuthController(WebAppService webAppService) {
        this.webAppService = webAppService;

    }


    @GetMapping("/signin")
    public ModelAndView signin() {

        return new ModelAndView("signin");
    }

//    @GetMapping("/signup")
//    public ModelAndView signup(Model model) {
//
//        model.addAttribute("signupForm", new SignupForm());
//
//        return new ModelAndView("signup");
//    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute("signupForm") SignupForm form,
                                BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "signup";
        }

        if (!java.util.Objects.equals(form.getPassword(), form.getConfirmPassword())) {
            bindingResult.rejectValue(
                    "confirmPassword",
                    "error.confirmPassword",
                    "Les mots de passe ne correspondent pas"
            );

            return "signup";
        }

        User created = webAppService.createUser(form);

        return "redirect:/signin";
    }


//    @PostMapping("/create-report")
//    public String submitReportStep1(@ModelAttribute ReportForm form){
//
//
//        ReportResponse report = webAppService.submitReportStep1(form);
//
//        session.setAttribute("report", report);
//
//
//        return "redirect:/create-report/map";
//
//    }
}