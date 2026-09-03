package com.medical.web_app.controller;

import com.medical.web_app.model.ProfessionalClient;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
public class ReportController {

    private final WebAppService webAppService;

    public ReportController(WebAppService webAppService) {
        this.webAppService = webAppService;
    }


    // =========================================================
    // PAGE REPORT
    // =========================================================

    @GetMapping("/report")
    public String reportPage(
            Model model,
            Principal principal) {

        String username = principal.getName();

        User user = webAppService.getUser(username)
                .orElse(null);

        model.addAttribute("user", user);

        return "report";
    }


    // =========================================================
    // ÉTAPE 1 - AFFICHAGE
    // =========================================================

    @GetMapping("/create-report")
    public String createReport(Model model) {

        model.addAttribute(
                "reportForm",
                new ReportForm()
        );

        return "create-report";
    }


    // =========================================================
    // ÉTAPE 1 - TRAITEMENT
    // =========================================================

    @PostMapping("/create-report")
    public String submitReportStep1(
            @ModelAttribute ReportForm form,
            @RequestParam(
                    value = "images",
                    required = false
            )
            List<MultipartFile> images,
            Principal principal,
            HttpSession session) {

        String username = principal.getName();

        User user = webAppService.getUser(username)
                .orElse(null);

        if (user == null) {
            return "redirect:/signin";
        }


        // -----------------------------------------------------
        // CRÉATION DU REPORT TEMPORAIRE
        // -----------------------------------------------------

        CreateReportTemp report =
                new CreateReportTemp();

        report.setUserId(user.getId());
        report.setTitle(form.getTitle());
        report.setDescription(form.getDescription());
        report.setCategory(form.getCategory());
        report.setGravity(form.getGravity());

        session.setAttribute(
                "report",
                report
        );


        // -----------------------------------------------------
        // CONSERVATION TEMPORAIRE DES IMAGES
        // -----------------------------------------------------

        List<byte[]> reportImageBytes =
                new ArrayList<>();

        List<String> reportImageTypes =
                new ArrayList<>();

        List<String> reportImageNames =
                new ArrayList<>();


        if (images != null && !images.isEmpty()) {

            for (MultipartFile image : images) {

                if (image != null && !image.isEmpty()) {

                    try {

                        reportImageBytes.add(
                                image.getBytes()
                        );

                        reportImageTypes.add(
                                image.getContentType()
                        );

                        reportImageNames.add(
                                image.getOriginalFilename()
                        );

                    } catch (IOException e) {

                        System.out.println(
                                "ERREUR LECTURE IMAGE = "
                                        + image.getOriginalFilename()
                        );

                        e.printStackTrace();
                    }
                }
            }
        }


        session.setAttribute(
                "reportImageBytes",
                reportImageBytes
        );

        session.setAttribute(
                "reportImageTypes",
                reportImageTypes
        );

        session.setAttribute(
                "reportImageNames",
                reportImageNames
        );


        System.out.println(
                "NOMBRE D'IMAGES TEMPORAIRES = "
                        + reportImageBytes.size()
        );

        System.out.println(
                "STEP 1 OK"
        );


        return "redirect:/create-report-step-0";
    }


    // =========================================================
    // ÉTAPE 2 - AFFICHAGE
    // =========================================================

    @GetMapping("/create-report-step-0")
    public String submitReportStep2(
            Model model,
            HttpSession session,
            Principal principal) {

        String username = principal.getName();

        User user = webAppService.getUser(username)
                .orElse(null);

        if (user == null) {
            return "redirect:/signin";
        }


        CreateReportTemp report =
                (CreateReportTemp)
                        session.getAttribute("report");


        if (report == null) {
            return "redirect:/create-report";
        }


        model.addAttribute(
                "user",
                user
        );

        model.addAttribute(
                "activeClient",
                user.getActiveClient()
        );


        return "create-report-step-0";
    }


    // =========================================================
    // ÉTAPE 2 - TRAITEMENT
    // =========================================================

    @PostMapping("/create-report-step-0")
    public String submitStep2(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam String location,
            @RequestParam Integer clientId,
            HttpSession session) {

        CreateReportTemp report =
                (CreateReportTemp)
                        session.getAttribute("report");


        if (report == null) {
            return "redirect:/create-report";
        }


        report.setLatitude(latitude);
        report.setLongitude(longitude);
        report.setLocation(location);
        report.setClientId(clientId);


        session.setAttribute(
                "report",
                report
        );


        System.out.println(
                "STEP 2 OK"
        );

        System.out.println(
                "SESSION = "
                        + session.getAttribute("report")
        );


        return "redirect:/create-report-step-1";
    }


    // =========================================================
    // ÉTAPE 3 - AFFICHAGE / CONFIRMATION
    // =========================================================

    @GetMapping("/create-report-step-1")
    public String submitReportStep3(
            Model model,
            HttpSession session) {

        CreateReportTemp report =
                (CreateReportTemp)
                        session.getAttribute("report");


        if (report == null) {
            return "redirect:/create-report";
        }


        model.addAttribute(
                "report",
                report
        );


        // -----------------------------------------------------
        // CLIENT
        // -----------------------------------------------------

        ProfessionalClient client =
                webAppService.getClientById(
                        report.getClientId()
                );


        model.addAttribute(
                "client",
                client
        );


        // -----------------------------------------------------
        // IMAGES DU SIGNALEMENT
        // -----------------------------------------------------

        List<byte[]> imageBytes =
                (List<byte[]>)
                        session.getAttribute(
                                "reportImageBytes"
                        );


        List<String> imageTypes =
                (List<String>)
                        session.getAttribute(
                                "reportImageTypes"
                        );


        List<String> reportImages =
                new ArrayList<>();


        if (imageBytes != null
                && imageTypes != null) {

            int count = Math.min(
                    imageBytes.size(),
                    imageTypes.size()
            );


            for (int i = 0; i < count; i++) {

                byte[] bytes =
                        imageBytes.get(i);

                String type =
                        imageTypes.get(i);


                if (bytes != null
                        && type != null) {

                    String base64 =
                            Base64.getEncoder()
                                    .encodeToString(bytes);


                    reportImages.add(
                            "data:"
                                    + type
                                    + ";base64,"
                                    + base64
                    );
                }
            }
        }


        model.addAttribute(
                "reportImages",
                reportImages
        );


        System.out.println(
                "IMAGES AFFICHABLES ETAPE 3 = "
                        + reportImages.size()
        );


        return "create-report-step-1";
    }


    // =========================================================
    // ÉTAPE 3 - ENVOI FINAL
    // =========================================================

    @PostMapping("/create-report-step-1")
    public String finalSubmit(
            HttpSession session,
            RedirectAttributes redirectAttributes) {


        CreateReportTemp report =
                (CreateReportTemp)
                        session.getAttribute("report");


        if (report == null) {
            return "redirect:/create-report";
        }


        // -----------------------------------------------------
        // RÉCUPÉRATION DES IMAGES TEMPORAIRES
        // -----------------------------------------------------

        List<byte[]> imageBytes =
                (List<byte[]>)
                        session.getAttribute(
                                "reportImageBytes"
                        );


        List<String> imageTypes =
                (List<String>)
                        session.getAttribute(
                                "reportImageTypes"
                        );


        List<String> imageNames =
                (List<String>)
                        session.getAttribute(
                                "reportImageNames"
                        );


        int imageCount = 0;


        if (imageBytes != null) {

            imageCount =
                    imageBytes.size();
        }


        System.out.println(
                "NOMBRE D'IMAGES AVANT ENVOI = "
                        + imageCount
        );


        // -----------------------------------------------------
        // 1. CRÉATION DU REPORT
        // -----------------------------------------------------

        ReportResponse response =
                webAppService.submitFinal(
                        report
                );


        Integer reportId =
                response.getId();


        System.out.println(
                "REPORT CREE AVEC ID = "
                        + reportId
        );


        // -----------------------------------------------------
        // 2. ENREGISTREMENT DES IMAGES
        // -----------------------------------------------------

        if (imageBytes != null
                && imageTypes != null
                && !imageBytes.isEmpty()) {


            int count = Math.min(
                    imageBytes.size(),
                    imageTypes.size()
            );


            for (int i = 0; i < count; i++) {

                byte[] bytes =
                        imageBytes.get(i);


                String contentType =
                        imageTypes.get(i);


                String originalFilename =
                        "image-" + (i + 1);


                if (imageNames != null
                        && imageNames.size() > i
                        && imageNames.get(i) != null
                        && !imageNames.get(i).isBlank()) {

                    originalFilename =
                            imageNames.get(i);
                }


                if (bytes == null
                        || bytes.length == 0) {

                    continue;
                }


                System.out.println(
                        "ENVOI IMAGE = "
                                + originalFilename
                );


                System.out.println(
                        "TYPE = "
                                + contentType
                );


                System.out.println(
                        "TAILLE = "
                                + bytes.length
                );


                // -------------------------------------------------
                // RECONSTRUCTION DU MULTIPARTFILE
                // -------------------------------------------------

                MultipartFile multipartFile =
                        new ByteArrayMultipartFile(
                                "file",
                                originalFilename,
                                contentType,
                                bytes
                        );


                // -------------------------------------------------
                // ENVOI VERS PATIENT-SERVICE
                // -------------------------------------------------

                String storageName =
                        webAppService.uploadReportImage(
                                reportId,
                                multipartFile
                        );


                System.out.println(
                        "IMAGE STOCKEE = "
                                + storageName
                );
            }
        }


        // -----------------------------------------------------
        // 3. NETTOYAGE DE LA SESSION
        // -----------------------------------------------------

        session.removeAttribute(
                "report"
        );

        session.removeAttribute(
                "reportImageBytes"
        );

        session.removeAttribute(
                "reportImageTypes"
        );

        session.removeAttribute(
                "reportImageNames"
        );


        // -----------------------------------------------------
        // MESSAGE DE SUCCÈS
        // -----------------------------------------------------

        redirectAttributes.addFlashAttribute(
                "success",
                "Votre signalement a bien été enregistré."
        );


        System.out.println(
                "REPORT FINAL SUBMITTED"
        );


        return "redirect:/report-success";
    }


    // =========================================================
    // PAGE SUCCÈS
    // =========================================================

    @GetMapping("/report-success")
    public String reportSuccess() {

        return "report-success";
    }


    // =========================================================
    // LISTE DES REPORTS
    // =========================================================

    @GetMapping("/report-list")
    public String reportList() {

        return "report-list";
    }

}