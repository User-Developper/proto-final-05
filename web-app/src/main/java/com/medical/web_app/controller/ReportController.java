package com.medical.web_app.controller;

import com.medical.web_app.model.ProfessionalClient;
import com.medical.web_app.model.User;
import com.medical.web_app.service.WebAppService;
import com.medical.web_app.service.dto.CreateReportTemp;
import com.medical.web_app.service.dto.ReportImageResponse;
import com.medical.web_app.service.dto.ReportResponse;
import com.medical.web_app.service.form.ReportForm;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/report")
    public String reportPage(
            @RequestParam Integer id,
            Model model) {

        ReportResponse report = webAppService.getReportById(id);

        if (report == null) {
            return "redirect:/report-list";
        }

        User author = null;

        if (report.getUserId() != null) {
            author = webAppService.getUserById(report.getUserId());
        }

        List<ReportImageResponse> reportImages =
                webAppService.getReportImages(id);

        if (reportImages == null) {
            reportImages = new ArrayList<>();
        }

        model.addAttribute("report", report);
        model.addAttribute("author", author);
        model.addAttribute("reportImages", reportImages);

        return "report";
    }

    @GetMapping("/create-report")
    public String createReport(Model model) {

        model.addAttribute("reportForm", new ReportForm());

        return "create-report";
    }

    @PostMapping("/create-report")
    public String submitReportStep1(
            @ModelAttribute ReportForm form,
            @RequestParam(value = "images", required = false)
            List<MultipartFile> images,
            Principal principal,
            HttpSession session) {

        String username = principal.getName();

        User user = webAppService.getUser(username)
                .orElse(null);

        if (user == null) {
            return "redirect:/signin";
        }

        CreateReportTemp report = new CreateReportTemp();

        report.setUserId(user.getId());
        report.setTitle(form.getTitle());
        report.setDescription(form.getDescription());
        report.setCategory(form.getCategory());
        report.setGravity(form.getGravity());

        session.setAttribute("report", report);

        List<byte[]> reportImageBytes = new ArrayList<>();
        List<String> reportImageTypes = new ArrayList<>();
        List<String> reportImageNames = new ArrayList<>();

        if (images != null && !images.isEmpty()) {

            for (MultipartFile image : images) {

                if (image != null && !image.isEmpty()) {

                    try {
                        reportImageBytes.add(image.getBytes());
                        reportImageTypes.add(image.getContentType());
                        reportImageNames.add(image.getOriginalFilename());

                    } catch (IOException e) {
                        System.out.println(
                                "ERREUR LECTURE IMAGE = "
                                        + image.getOriginalFilename());

                        e.printStackTrace();
                    }
                }
            }
        }

        session.setAttribute("reportImageBytes", reportImageBytes);
        session.setAttribute("reportImageTypes", reportImageTypes);
        session.setAttribute("reportImageNames", reportImageNames);

        System.out.println(
                "NOMBRE D'IMAGES TEMPORAIRES = "
                        + reportImageBytes.size());

        System.out.println("STEP 1 OK");

        return "redirect:/create-report-step-0";
    }

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
                (CreateReportTemp) session.getAttribute("report");

        if (report == null) {
            return "redirect:/create-report";
        }

        model.addAttribute("user", user);
        model.addAttribute("activeClient", user.getActiveClient());

        return "create-report-step-0";
    }

    @PostMapping("/create-report-step-0")
    public String submitStep2(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam String location,
            @RequestParam Integer clientId,
            HttpSession session) {

        CreateReportTemp report =
                (CreateReportTemp) session.getAttribute("report");

        if (report == null) {
            return "redirect:/create-report";
        }

        report.setLatitude(latitude);
        report.setLongitude(longitude);
        report.setLocation(location);
        report.setClientId(clientId);

        session.setAttribute("report", report);

        System.out.println("STEP 2 OK");
        System.out.println("SESSION = " + session.getAttribute("report"));

        return "redirect:/create-report-step-1";
    }

    @GetMapping("/create-report-step-1")
    public String submitReportStep3(
            Model model,
            HttpSession session) {

        CreateReportTemp report =
                (CreateReportTemp) session.getAttribute("report");

        if (report == null) {
            return "redirect:/create-report";
        }

        model.addAttribute("report", report);

        ProfessionalClient client = webAppService.getClientById(
                report.getClientId());

        model.addAttribute("client", client);

        List<byte[]> imageBytes =
                (List<byte[]>) session.getAttribute("reportImageBytes");

        List<String> imageTypes =
                (List<String>) session.getAttribute("reportImageTypes");

        List<String> reportImages = new ArrayList<>();

        if (imageBytes != null && imageTypes != null) {

            int count = Math.min(
                    imageBytes.size(),
                    imageTypes.size());

            for (int i = 0; i < count; i++) {

                byte[] bytes = imageBytes.get(i);
                String type = imageTypes.get(i);

                if (bytes != null && type != null) {

                    String base64 = Base64.getEncoder()
                            .encodeToString(bytes);

                    reportImages.add(
                            "data:"
                                    + type
                                    + ";base64,"
                                    + base64);
                }
            }
        }

        model.addAttribute("reportImages", reportImages);

        System.out.println(
                "IMAGES AFFICHABLES ETAPE 3 = "
                        + reportImages.size());

        return "create-report-step-1";
    }

    @PostMapping("/create-report-step-1")
    public String finalSubmit(
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        CreateReportTemp report =
                (CreateReportTemp) session.getAttribute("report");

        if (report == null) {
            return "redirect:/create-report";
        }

        List<byte[]> imageBytes =
                (List<byte[]>) session.getAttribute("reportImageBytes");

        List<String> imageTypes =
                (List<String>) session.getAttribute("reportImageTypes");

        List<String> imageNames =
                (List<String>) session.getAttribute("reportImageNames");

        int imageCount = 0;

        if (imageBytes != null) {
            imageCount = imageBytes.size();
        }

        System.out.println(
                "NOMBRE D'IMAGES AVANT ENVOI = "
                        + imageCount);

        ReportResponse response = webAppService.submitFinal(report);

        Integer reportId = response.getId();

        System.out.println(
                "REPORT CREE AVEC ID = "
                        + reportId);

        if (imageBytes != null
                && imageTypes != null
                && !imageBytes.isEmpty()) {

            int count = Math.min(
                    imageBytes.size(),
                    imageTypes.size());

            for (int i = 0; i < count; i++) {

                byte[] bytes = imageBytes.get(i);
                String contentType = imageTypes.get(i);

                String originalFilename = "image-" + (i + 1);

                if (imageNames != null
                        && imageNames.size() > i
                        && imageNames.get(i) != null
                        && !imageNames.get(i).isBlank()) {

                    originalFilename = imageNames.get(i);
                }

                if (bytes == null || bytes.length == 0) {
                    continue;
                }

                System.out.println(
                        "ENVOI IMAGE = "
                                + originalFilename);

                System.out.println(
                        "TYPE = "
                                + contentType);

                System.out.println(
                        "TAILLE = "
                                + bytes.length);

                MultipartFile multipartFile = new ByteArrayMultipartFile(
                        "file",
                        originalFilename,
                        contentType,
                        bytes);

                String storageName = webAppService.uploadReportImage(
                        reportId,
                        multipartFile);

                System.out.println(
                        "IMAGE STOCKEE = "
                                + storageName);
            }
        }

        session.removeAttribute("report");
        session.removeAttribute("reportImageBytes");
        session.removeAttribute("reportImageTypes");
        session.removeAttribute("reportImageNames");

        redirectAttributes.addFlashAttribute(
                "success",
                "Votre signalement a bien été enregistré.");

        System.out.println("REPORT FINAL SUBMITTED");

        return "redirect:/report-success";
    }

    @GetMapping("/report-success")
    public String reportSuccess() {

        return "report-success";
    }

    @GetMapping("/report-list")
    public String reportList() {

        return "report-list";
    }

    @GetMapping("/report-images/{reportId}/{imageId}")
    public ResponseEntity<byte[]> getReportImage(
            @PathVariable Integer reportId,
            @PathVariable String imageId) {

        List<ReportImageResponse> images =
                webAppService.getReportImages(reportId);

        if (images == null) {
            return ResponseEntity.notFound().build();
        }

        ReportImageResponse metadata = images.stream()
                .filter(image -> imageId.equals(image.getId()))
                .findFirst()
                .orElse(null);

        if (metadata == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] image = webAppService.getReportImage(reportId, imageId);

        if (image == null || image.length == 0) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

        if (metadata.getContentType() != null
                && !metadata.getContentType().isBlank()) {

            try {
                mediaType = MediaType.parseMediaType(
                        metadata.getContentType());

            } catch (IllegalArgumentException ignored) {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }
        }

        String fileName = metadata.getFileName();

        if (fileName == null || fileName.isBlank()) {
            fileName = "image";
        }

        ContentDisposition disposition = ContentDisposition.inline()
                .filename(fileName)
                .build();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        disposition.toString())
                .contentType(mediaType)
                .body(image);
    }

    @GetMapping("/conditions-utilisation")
    public String conditionsUtilisation() {

        return "conditions-utilisation";
    }

    @GetMapping("/politique-confidentialite")
    public String politiqueConfidentialite() {

        return "politique-confidentialite";
    }
}
