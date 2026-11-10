package com.medical.web_app.controller;

import com.medical.web_app.model.User;
import com.medical.web_app.service.WebAppService;
import com.medical.web_app.service.dto.CreateReportTemp;
import com.medical.web_app.service.dto.ReportResponse;
import com.medical.web_app.service.form.ReportForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private WebAppService webAppService;

    @InjectMocks
    private ReportController reportController;

    private Model model;
    private MockHttpSession session;
    private Principal principal;

    @BeforeEach
    void setUp() {
        model = new ExtendedModelMap();
        session = new MockHttpSession();
        principal = () -> "alice";
    }

    private User createUser() {
        User user = new User();
        user.setId(10L);
        return user;
    }

    private CreateReportTemp createReport() {
        CreateReportTemp report = new CreateReportTemp();
        report.setUserId(10L);
        report.setTitle("Signalement test");
        report.setDescription("Description test");
        report.setLocation("Paris");
        report.setClientId(5);
        return report;
    }

    private ReportResponse createReportResponse() {
        ReportResponse response = new ReportResponse();
        response.setId(1);
        return response;
    }

    // ==========================================
    // PAGE REPORT
    // ==========================================

    @Test
    void shouldRedirectWhenReportDoesNotExist() {
        when(webAppService.getReportById(1)).thenReturn(null);

        String result = reportController.reportPage(1, model);

        assertEquals("redirect:/report-list", result);
    }

    @Test
    void shouldDisplayReportWithoutAuthor() {
        ReportResponse report = mock(ReportResponse.class);
        when(report.getUserId()).thenReturn(null);

        when(webAppService.getReportById(1)).thenReturn(report);
        when(webAppService.getReportImages(1)).thenReturn(null);

        String result = reportController.reportPage(1, model);

        assertEquals("report", result);
        assertEquals(report, model.getAttribute("report"));
        assertNull(model.getAttribute("author"));
        assertTrue(((List<?>) model.getAttribute("reportImages")).isEmpty());

        verify(webAppService, never()).getUserById(anyLong());
    }

    @Test
    void shouldDisplayReportWithAuthor() {
        ReportResponse report = mock(ReportResponse.class);
        User author = createUser();

        when(report.getUserId()).thenReturn(10L);
        when(webAppService.getReportById(1)).thenReturn(report);
        when(webAppService.getUserById(10L)).thenReturn(author);
        when(webAppService.getReportImages(1)).thenReturn(List.of());

        String result = reportController.reportPage(1, model);

        assertEquals("report", result);
        assertEquals(author, model.getAttribute("author"));
    }

    // ==========================================
    // CRÉATION DU REPORT
    // ==========================================

    @Test
    void shouldDisplayCreateReportPage() {
        String result = reportController.createReport(model);

        assertEquals("create-report", result);
        assertNotNull(model.getAttribute("reportForm"));
    }

    @Test
    void shouldRedirectWhenUserDoesNotExist() {
        when(webAppService.getUser("alice")).thenReturn(Optional.empty());

        String result = reportController.submitReportStep1(
                mock(ReportForm.class),
                null,
                principal,
                session
        );

        assertEquals("redirect:/signin", result);
        assertNull(session.getAttribute("report"));
    }

    @Test
    void shouldCreateTemporaryReportWithoutImages() {
        User user = createUser();
        ReportForm form = mock(ReportForm.class);

        when(form.getTitle()).thenReturn("Titre test");
        when(form.getDescription()).thenReturn("Description test");
        when(webAppService.getUser("alice")).thenReturn(Optional.of(user));

        String result = reportController.submitReportStep1(
                form,
                null,
                principal,
                session
        );

        assertEquals("redirect:/create-report-step-0", result);

        CreateReportTemp report =
                (CreateReportTemp) session.getAttribute("report");

        assertNotNull(report);
        assertEquals(10L, report.getUserId());
        assertEquals("Titre test", report.getTitle());

        assertNotNull(session.getAttribute("reportImageBytes"));
        assertNotNull(session.getAttribute("reportImageTypes"));
        assertNotNull(session.getAttribute("reportImageNames"));
    }

    @Test
    void shouldSaveUploadedImageInSession() {
        User user = createUser();

        MockMultipartFile image = new MockMultipartFile(
                "images",
                "photo.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3}
        );

        when(webAppService.getUser("alice")).thenReturn(Optional.of(user));

        String result = reportController.submitReportStep1(
                mock(ReportForm.class),
                List.of(image),
                principal,
                session
        );

        assertEquals("redirect:/create-report-step-0", result);

        List<byte[]> images =
                (List<byte[]>) session.getAttribute("reportImageBytes");

        assertEquals(1, images.size());
        assertArrayEquals(new byte[]{1, 2, 3}, images.get(0));
    }

    // ==========================================
    // ÉTAPE 2 : AFFICHAGE
    // ==========================================

    @Test
    void shouldRedirectToSigninWhenUserDoesNotExistAtStep2() {
        when(webAppService.getUser("alice")).thenReturn(Optional.empty());

        String result = reportController.submitReportStep2(
                model,
                session,
                principal
        );

        assertEquals("redirect:/signin", result);
    }

    @Test
    void shouldRedirectWhenTemporaryReportDoesNotExistAtStep2() {
        when(webAppService.getUser("alice"))
                .thenReturn(Optional.of(createUser()));

        String result = reportController.submitReportStep2(
                model,
                session,
                principal
        );

        assertEquals("redirect:/create-report", result);
    }

    @Test
    void shouldDisplayStep2() {
        User user = createUser();
        session.setAttribute("report", createReport());

        when(webAppService.getUser("alice"))
                .thenReturn(Optional.of(user));

        String result = reportController.submitReportStep2(
                model,
                session,
                principal
        );

        assertEquals("create-report-step-0", result);
        assertEquals(user, model.getAttribute("user"));
    }

    // ==========================================
    // ÉTAPE 2 : TRAITEMENT
    // ==========================================

    @Test
    void shouldRedirectWhenReportIsMissingAtStep2Post() {
        String result = reportController.submitStep2(
                48.8566,
                2.3522,
                "Paris",
                5,
                session
        );

        assertEquals("redirect:/create-report", result);
    }

    @Test
    void shouldSaveLocationAndCoordinates() {
        CreateReportTemp report = createReport();
        session.setAttribute("report", report);

        String result = reportController.submitStep2(
                48.8566,
                2.3522,
                "Paris",
                8,
                session
        );

        assertEquals("redirect:/create-report-step-1", result);
        assertEquals(Double.valueOf(48.8566), report.getLatitude());
        assertEquals(Double.valueOf(2.3522), report.getLongitude());
        assertEquals("Paris", report.getLocation());
        assertEquals(Integer.valueOf(8), report.getClientId());
    }

    // ==========================================
    // ÉTAPE 3 : AFFICHAGE
    // ==========================================

    @Test
    void shouldRedirectWhenReportIsMissingAtStep3() {
        String result = reportController.submitReportStep3(model, session);

        assertEquals("redirect:/create-report", result);
    }

    @Test
    void shouldDisplayStep3WithoutImages() {
        session.setAttribute("report", createReport());

        when(webAppService.getClientById(5)).thenReturn(null);

        String result = reportController.submitReportStep3(model, session);

        assertEquals("create-report-step-1", result);
        assertNotNull(model.getAttribute("reportImages"));
        assertTrue(((List<?>) model.getAttribute("reportImages")).isEmpty());
    }

    @Test
    void shouldConvertImageToBase64() {
        session.setAttribute("report", createReport());
        session.setAttribute("reportImageBytes", List.of(new byte[]{1, 2, 3}));
        session.setAttribute("reportImageTypes", List.of("image/jpeg"));

        when(webAppService.getClientById(5)).thenReturn(null);

        String result = reportController.submitReportStep3(model, session);

        assertEquals("create-report-step-1", result);

        List<String> images =
                (List<String>) model.getAttribute("reportImages");

        assertEquals(1, images.size());
        assertTrue(images.get(0).startsWith("data:image/jpeg;base64,"));
    }

    // ==========================================
    // ENVOI FINAL
    // ==========================================

    @Test
    void shouldRedirectWhenReportIsMissingAtFinalSubmit() {
        String result = reportController.finalSubmit(
                session,
                new RedirectAttributesModelMap()
        );

        assertEquals("redirect:/create-report", result);
        verify(webAppService, never()).submitFinal(any());
    }

    @Test
    void shouldSubmitReportWithoutImages() {
        CreateReportTemp report = createReport();
        session.setAttribute("report", report);

        when(webAppService.submitFinal(report))
                .thenReturn(createReportResponse());

        RedirectAttributesModelMap redirectAttributes =
                new RedirectAttributesModelMap();

        String result = reportController.finalSubmit(
                session,
                redirectAttributes
        );

        assertEquals("redirect:/report-success", result);
        verify(webAppService).submitFinal(report);

        assertNull(session.getAttribute("report"));

        assertEquals(
                "Votre signalement a bien été enregistré.",
                redirectAttributes.getFlashAttributes().get("success")
        );
    }

    @Test
    void shouldUploadImageWhenSubmittingReport() {
        CreateReportTemp report = createReport();
        session.setAttribute("report", report);

        session.setAttribute("reportImageBytes", List.of(new byte[]{1, 2, 3}));
        session.setAttribute("reportImageTypes", List.of("image/jpeg"));
        session.setAttribute("reportImageNames", List.of("photo.jpg"));

        when(webAppService.submitFinal(report))
                .thenReturn(createReportResponse());

        when(webAppService.uploadReportImage(
                eq(1),
                any(MultipartFile.class)
        )).thenReturn("stored-photo.jpg");

        String result = reportController.finalSubmit(
                session,
                new RedirectAttributesModelMap()
        );

        assertEquals("redirect:/report-success", result);

        verify(webAppService).uploadReportImage(
                eq(1),
                any(MultipartFile.class)
        );
    }

    // ==========================================
    // PAGES STATIQUES
    // ==========================================

    @Test
    void shouldDisplayReportSuccessPage() {
        assertEquals("report-success", reportController.reportSuccess());
    }

    @Test
    void shouldDisplayReportListPage() {
        assertEquals("report-list", reportController.reportList());
    }

    // ==========================================
    // RÉCUPÉRATION DES IMAGES
    // ==========================================

    @Test
    void shouldReturnNotFoundWhenImageDoesNotExist() {
        when(webAppService.getReportImage(1, "photo.jpg"))
                .thenReturn(null);

        var response = reportController.getReportImage(1, "photo.jpg");

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void shouldReturnNotFoundWhenImageIsEmpty() {
        when(webAppService.getReportImage(1, "photo.jpg"))
                .thenReturn(new byte[0]);

        var response = reportController.getReportImage(1, "photo.jpg");

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void shouldReturnJpegImage() {
        byte[] image = {1, 2, 3};

        when(webAppService.getReportImage(1, "photo.jpg"))
                .thenReturn(image);

        var response = reportController.getReportImage(1, "photo.jpg");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertArrayEquals(image, response.getBody());
    }

    @Test
    void shouldReturnPngImage() {
        when(webAppService.getReportImage(1, "photo.png"))
                .thenReturn(new byte[]{1});

        var response = reportController.getReportImage(1, "photo.png");

        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
    }

    @Test
    void shouldReturnGifImage() {
        when(webAppService.getReportImage(1, "photo.gif"))
                .thenReturn(new byte[]{1});

        var response = reportController.getReportImage(1, "photo.gif");

        assertEquals(MediaType.IMAGE_GIF, response.getHeaders().getContentType());
    }

    @Test
    void shouldReturnWebpImage() {
        when(webAppService.getReportImage(1, "photo.webp"))
                .thenReturn(new byte[]{1});

        var response = reportController.getReportImage(1, "photo.webp");

        assertEquals("image/webp", response.getHeaders().getContentType().toString());
    }

    @Test
    void shouldReturnDefaultContentTypeForUnknownExtension() {
        when(webAppService.getReportImage(1, "document.pdf"))
                .thenReturn(new byte[]{1});

        var response = reportController.getReportImage(1, "document.pdf");

        assertEquals(
                MediaType.APPLICATION_OCTET_STREAM,
                response.getHeaders().getContentType()
        );
    }
}
