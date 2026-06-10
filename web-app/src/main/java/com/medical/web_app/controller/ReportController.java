package com.medical.web_app.controller;


import com.medical.web_app.model.User;
import com.medical.web_app.service.WebAppService;
import com.medical.web_app.service.dto.CreateReportTemp;
import com.medical.web_app.service.dto.ReportResponse;
import com.medical.web_app.service.form.ReportForm;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/report-list")
    public String reportList(Model model) {

        List<ReportResponse> reports =
                webAppService.getAllReports();

        model.addAttribute("reports", reports);

        return "report-list";
    }
}
