package com.medical.web_app.controller;


import com.medical.web_app.model.Category;
import com.medical.web_app.model.Gravity;
import com.medical.web_app.model.User;
import com.medical.web_app.service.WebAppService;
import com.medical.web_app.service.dto.CreateReportTemp;
import com.medical.web_app.service.dto.PageResponse;
import com.medical.web_app.service.dto.ReportResponse;
import com.medical.web_app.service.form.ReportForm;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class ReportController {

    private final WebAppService webAppService;


    public ReportController(WebAppService webAppService) {
        this.webAppService = webAppService;

    }



    @GetMapping("/report")
    public String reportPage(Model model, Principal principal) {

        String username = principal.getName();

        User user = webAppService.getUser(username)
                .orElse(null);

        model.addAttribute("user", user);

        return "report";
    }

    @GetMapping("/create-report")
    public String createReport(Model model){

        model.addAttribute("reportForm", new ReportForm());

        return "create-report";


    }


    @PostMapping("/create-report")
    public String submitReportStep1(@ModelAttribute ReportForm form,
                                    HttpSession session) {

        CreateReportTemp report = new CreateReportTemp();

        report.setTitle(form.getTitle());
        report.setDescription(form.getDescription());
        report.setCategory(form.getCategory());
        report.setGravity(form.getGravity());

        session.setAttribute("report", report);

        System.out.println("STEP 1 OK");



        return "redirect:/create-report-step-0";


    }

    @GetMapping("/create-report-step-0")
    public String submitReportStep2(Model model, HttpSession session){


        return "create-report-step-0";


    }

    @PostMapping("/create-report-step-0")
    public String submitStep2(@RequestParam Double latitude,
                              @RequestParam Double longitude,
                              @RequestParam String location,
                              HttpSession session) {

        CreateReportTemp report =
                (CreateReportTemp) session.getAttribute("report");

        report.setLatitude(latitude);
        report.setLongitude(longitude);
        report.setLocation(location);

        session.setAttribute("report", report);

        System.out.println("STEP 2 OK");
        System.out.println("SESSION = " + session.getAttribute("report"));

        return "redirect:/create-report-step-1";
    }

    @GetMapping("/create-report-step-1")
    public String submitReportStep3(Model model, HttpSession session){

        CreateReportTemp report =
                (CreateReportTemp) session.getAttribute("report");

        model.addAttribute("report", report);


        return "create-report-step-1";


    }

    @PostMapping("/create-report-step-1")
    public String finalSubmit(HttpSession session, RedirectAttributes redirectAttributes) {

        CreateReportTemp report =
                (CreateReportTemp) session.getAttribute("report");

        if (report == null) {
            return "redirect:/create-report";
        }


        webAppService.submitFinal(report);

        session.removeAttribute("report");

        redirectAttributes.addFlashAttribute(
                "success",
                "Votre signalement a bien été enregistré."
        );

        System.out.println("REPORT FINAL SUBMITTED");

        return "redirect:/report-success";
    }

    @GetMapping("/report-success")
    public String reportSuccess() {

        return "report-success";

    }

//    @GetMapping("/report-list")
//    public String reportList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size, @RequestParam(required = false) Category category,
//                             @RequestParam(required = false) Gravity gravity,
//                             @RequestParam(required = false) String q,Model model) {
//
//        System.out.println("CATEGORY = " + category);
//        System.out.println("GRAVITY = " + gravity);
//        System.out.println("Q = " + q);
//
//        PageResponse<ReportResponse> reports =
//                webAppService.getAllReports(page, size, category, gravity, q);
//
//        model.addAttribute("reports", reports);
//        model.addAttribute("currentPage", reports.getNumber());
//        model.addAttribute("totalPages", reports.getTotalPages());
//
//        model.addAttribute("selectedCategory", category);
//        model.addAttribute("selectedGravity", gravity);
//        model.addAttribute("search", q);
//
//        return "report-list";
//    }

//    @GetMapping("/api")
//    public PageResponse<ReportResponse> getReports(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "6") int size,
//            @RequestParam(required = false) Category category,
//            @RequestParam(required = false) Gravity gravity,
//            @RequestParam(required = false) String q
//    ) {
//        return webAppService.getAllReports(page, size, category, gravity, q);
//    }

    @GetMapping("/report-list")
    public String reportList() {
        return "report-list";
    }

//    @GetMapping("/api/reports")
//    public PageResponse<ReportResponse> getReports(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "6") int size,
//            @RequestParam(required = false) Category category,
//            @RequestParam(required = false) Gravity gravity,
//            @RequestParam(required = false) String q
//    ) {
//        return webAppService.getAllReports(page, size, category, gravity, q);
//    }



}
