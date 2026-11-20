package com.medical.web_app;

import com.medical.web_app.controller.ReportController;
import com.medical.web_app.model.User;
import com.medical.web_app.service.WebAppService;
import com.medical.web_app.service.dto.CreateReportTemp;
import com.medical.web_app.service.dto.ReportImageResponse;
import com.medical.web_app.service.dto.ReportResponse;
import com.medical.web_app.service.form.ReportForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.security.Principal;
import java.util.Collections;
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

    private ExtendedModelMap model;
    private MockHttpSession session;
    private Principal principal;

    @BeforeEach
    void init() {
        model = new ExtendedModelMap();
        session = new MockHttpSession();
        principal = () -> "alice";
    }

    private User buildUser() {
        User user = new User();
        user.setId(10L);
        user.setUsername("alice");
        return user;
    }

    private CreateReportTemp buildTempReport() {
        CreateReportTemp report = new CreateReportTemp();
        report.setUserId(10L);
        report.setTitle("Test report");
        report.setDescription("Description test");
        report.setLocation("Paris");
        report.setClientId(5);
        return report;
    }

    private ReportResponse buildReportResponse() {
        ReportResponse response = new ReportResponse();
        response.setId(1);
        response.setUserId(10L);
        return response;
    }

    private ReportImageResponse mockImageMeta(String id, String contentType, String fileName) {
        ReportImageResponse meta = mock(ReportImageResponse.class);
        when(meta.getId()).thenReturn(id);
        lenient().when(meta.getContentType()).thenReturn(contentType);
        lenient().when(meta.getFileName()).thenReturn(fileName);
        return meta;
    }

    @Test
    void pageReportOk() {
        ReportResponse report = buildReportResponse();
        User author = buildUser();

        when(webAppService.getReportById(1)).thenReturn(report);
        when(webAppService.getUserById(10L)).thenReturn(author);
        when(webAppService.getReportImages(1)).thenReturn(List.of());

        String view = reportController.reportPage(1, model);

        assertEquals("report", view);
        assertEquals(report, model.getAttribute("report"));
        assertEquals(author, model.getAttribute("author"));
        assertEquals(List.of(), model.getAttribute("reportImages"));
    }

    @Test
    void pageReportInexistantRedirige() {
        when(webAppService.getReportById(1)).thenReturn(null);

        String view = reportController.reportPage(1, model);

        assertEquals("redirect:/report-list", view);
    }

    @Test
    void pageReportSansAuteur() {
        ReportResponse report = buildReportResponse();
        when(webAppService.getReportById(1)).thenReturn(report);
        when(webAppService.getUserById(10L)).thenReturn(null);
        when(webAppService.getReportImages(1)).thenReturn(List.of());

        String view = reportController.reportPage(1, model);

        assertEquals("report", view);
        assertNull(model.getAttribute("author"));
    }

    @Test
    void pageReportImagesNull() {
        ReportResponse report = buildReportResponse();
        when(webAppService.getReportById(1)).thenReturn(report);
        when(webAppService.getUserById(10L)).thenReturn(buildUser());
        when(webAppService.getReportImages(1)).thenReturn(null);

        String view = reportController.reportPage(1, model);

        assertEquals("report", view);
        assertEquals(List.of(), model.getAttribute("reportImages"));
    }

    @Test
    void pageCreationReport() {
        String view = reportController.createReport(model);

        assertEquals("create-report", view);
        assertNotNull(model.getAttribute("reportForm"));
    }

    @Test
    void step1UserInconnu() {
        when(webAppService.getUser("alice")).thenReturn(Optional.empty());

        String view = reportController.submitReportStep1(new ReportForm(), null, principal, session);

        assertEquals("redirect:/signin", view);
    }

    @Test
    void step1SansImage() {
        when(webAppService.getUser("alice")).thenReturn(Optional.of(buildUser()));

        String view = reportController.submitReportStep1(new ReportForm(), null, principal, session);

        assertEquals("redirect:/create-report-step-0", view);
        assertNotNull(session.getAttribute("report"));
        assertEquals(List.of(), session.getAttribute("reportImageBytes"));
        assertEquals(List.of(), session.getAttribute("reportImageTypes"));
        assertEquals(List.of(), session.getAttribute("reportImageNames"));
    }

    @Test
    void step1AvecImage() {
        when(webAppService.getUser("alice")).thenReturn(Optional.of(buildUser()));
        MockMultipartFile image = new MockMultipartFile("images", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});

        String view = reportController.submitReportStep1(new ReportForm(), List.of(image), principal, session);

        assertEquals("redirect:/create-report-step-0", view);
        List<byte[]> images = (List<byte[]>) session.getAttribute("reportImageBytes");
        assertEquals(1, images.size());
        assertArrayEquals(new byte[]{1, 2, 3}, images.get(0));
    }

    @Test
    void step1IgnoreImageVide() {
        when(webAppService.getUser("alice")).thenReturn(Optional.of(buildUser()));
        MockMultipartFile emptyImage = new MockMultipartFile("images", "empty.jpg", "image/jpeg", new byte[0]);

        String view = reportController.submitReportStep1(new ReportForm(), List.of(emptyImage), principal, session);

        assertEquals("redirect:/create-report-step-0", view);
        List<byte[]> images = (List<byte[]>) session.getAttribute("reportImageBytes");
        assertTrue(images.isEmpty());
    }

    @Test
    void step2UserInconnu() {
        when(webAppService.getUser("alice")).thenReturn(Optional.empty());

        String view = reportController.submitReportStep2(model, session, principal);

        assertEquals("redirect:/signin", view);
    }

    @Test
    void step2SansReportEnSession() {
        when(webAppService.getUser("alice")).thenReturn(Optional.of(buildUser()));

        String view = reportController.submitReportStep2(model, session, principal);

        assertEquals("redirect:/create-report", view);
    }

    @Test
    void step2PageOk() {
        User user = buildUser();
        session.setAttribute("report", buildTempReport());
        when(webAppService.getUser("alice")).thenReturn(Optional.of(user));

        String view = reportController.submitReportStep2(model, session, principal);

        assertEquals("create-report-step-0", view);
        assertEquals(user, model.getAttribute("user"));
    }

    @Test
    void soumissionStep2SansReport() {
        String view = reportController.submitStep2(48.8566, 2.3522, "Paris", 5, session);

        assertEquals("redirect:/create-report", view);
    }

    @Test
    void soumissionStep2Ok() {
        CreateReportTemp report = buildTempReport();
        session.setAttribute("report", report);

        String view = reportController.submitStep2(48.8566, 2.3522, "Paris", 5, session);

        assertEquals("redirect:/create-report-step-1", view);
        assertEquals(48.8566, report.getLatitude());
        assertEquals(2.3522, report.getLongitude());
        assertEquals("Paris", report.getLocation());
        assertEquals(5, report.getClientId());
    }

    @Test
    void step3SansReport() {
        String view = reportController.submitReportStep3(model, session);

        assertEquals("redirect:/create-report", view);
    }

    @Test
    void step3PageOk() {
        CreateReportTemp report = buildTempReport();
        session.setAttribute("report", report);
        when(webAppService.getClientById(5)).thenReturn(null);

        String view = reportController.submitReportStep3(model, session);

        assertEquals("create-report-step-1", view);
        assertEquals(report, model.getAttribute("report"));
        assertNull(model.getAttribute("client"));
        assertEquals(List.of(), model.getAttribute("reportImages"));
    }

    @Test
    void step3AvecImagesTemporaires() {
        session.setAttribute("report", buildTempReport());
        session.setAttribute("reportImageBytes", List.of(new byte[]{1, 2, 3}));
        session.setAttribute("reportImageTypes", List.of("image/jpeg"));
        when(webAppService.getClientById(5)).thenReturn(null);

        String view = reportController.submitReportStep3(model, session);

        assertEquals("create-report-step-1", view);
        List<String> images = (List<String>) model.getAttribute("reportImages");
        assertEquals(1, images.size());
        assertTrue(images.get(0).startsWith("data:image/jpeg;base64,"));
    }

    @Test
    void step3SansTypesImages() {
        session.setAttribute("report", buildTempReport());
        session.setAttribute("reportImageBytes", List.of(new byte[]{1, 2, 3}));
        when(webAppService.getClientById(5)).thenReturn(null);

        String view = reportController.submitReportStep3(model, session);

        assertEquals("create-report-step-1", view);
        assertEquals(List.of(), model.getAttribute("reportImages"));
    }

    @Test
    void step3ImagesOuTypesNull() {
        session.setAttribute("report", buildTempReport());
        session.setAttribute("reportImageBytes", Collections.singletonList((byte[]) null));
        session.setAttribute("reportImageTypes", Collections.singletonList((String) null));
        when(webAppService.getClientById(5)).thenReturn(null);

        String view = reportController.submitReportStep3(model, session);

        assertEquals("create-report-step-1", view);
        assertEquals(List.of(), model.getAttribute("reportImages"));
    }

    @Test
    void finalSubmitSansReport() {
        String view = reportController.finalSubmit(session, new RedirectAttributesModelMap());

        assertEquals("redirect:/create-report", view);
    }

    @Test
    void finalSubmitSansImages() {
        CreateReportTemp report = buildTempReport();
        session.setAttribute("report", report);
        when(webAppService.submitFinal(report)).thenReturn(buildReportResponse());

        String view = reportController.finalSubmit(session, new RedirectAttributesModelMap());

        assertEquals("redirect:/report-success", view);
        verify(webAppService).submitFinal(report);
        assertNull(session.getAttribute("report"));
    }

    @Test
    void finalSubmitAvecImages() {
        CreateReportTemp report = buildTempReport();
        session.setAttribute("report", report);
        session.setAttribute("reportImageBytes", List.of(new byte[]{1, 2, 3}));
        session.setAttribute("reportImageTypes", List.of("image/jpeg"));
        session.setAttribute("reportImageNames", List.of("photo.jpg"));

        when(webAppService.submitFinal(report)).thenReturn(buildReportResponse());
        when(webAppService.uploadReportImage(eq(1), any(MultipartFile.class)))
                .thenReturn("stored-photo.jpg");

        String view = reportController.finalSubmit(session, new RedirectAttributesModelMap());

        assertEquals("redirect:/report-success", view);
        verify(webAppService).submitFinal(report);
        verify(webAppService).uploadReportImage(eq(1), any(MultipartFile.class));
        assertNull(session.getAttribute("report"));
        assertNull(session.getAttribute("reportImageBytes"));
        assertNull(session.getAttribute("reportImageTypes"));
        assertNull(session.getAttribute("reportImageNames"));
    }

    @Test
    void finalSubmitNomFichierParDefaut() {
        CreateReportTemp report = buildTempReport();
        session.setAttribute("report", report);
        session.setAttribute("reportImageBytes", List.of(new byte[]{1, 2, 3}));
        session.setAttribute("reportImageTypes", List.of("image/jpeg"));
        session.setAttribute("reportImageNames", List.of(" "));

        when(webAppService.submitFinal(report)).thenReturn(buildReportResponse());
        when(webAppService.uploadReportImage(eq(1), any(MultipartFile.class)))
                .thenReturn("stored-photo.jpg");

        String view = reportController.finalSubmit(session, new RedirectAttributesModelMap());

        assertEquals("redirect:/report-success", view);
        verify(webAppService).uploadReportImage(
                eq(1),
                argThat(file -> "image-1".equals(file.getOriginalFilename()))
        );
    }

    @Test
    void finalSubmitIgnoreOctetsVides() {
        CreateReportTemp report = buildTempReport();
        session.setAttribute("report", report);
        session.setAttribute("reportImageBytes", List.of(new byte[0]));
        session.setAttribute("reportImageTypes", List.of("image/jpeg"));
        when(webAppService.submitFinal(report)).thenReturn(buildReportResponse());

        String view = reportController.finalSubmit(session, new RedirectAttributesModelMap());

        assertEquals("redirect:/report-success", view);
        verify(webAppService, never()).uploadReportImage(anyInt(), any(MultipartFile.class));
    }

    @Test
    void pagesStatiques() {
        assertEquals("report-success", reportController.reportSuccess());
        assertEquals("report-list", reportController.reportList());
        assertEquals("conditions-utilisation", reportController.conditionsUtilisation());
        assertEquals("politique-confidentialite", reportController.politiqueConfidentialite());
    }

    @Test
    void getImageListeNull() {
        when(webAppService.getReportImages(1)).thenReturn(null);

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(404, response.getStatusCode().value());
        verify(webAppService, never()).getReportImage(anyInt(), anyString());
    }

    @Test
    void getImageAbsenteDeLaListe() {
        ReportImageResponse meta = mockImageMeta("photo-2", "image/jpeg", "photo.jpg");
        when(webAppService.getReportImages(1)).thenReturn(List.of(meta));

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(404, response.getStatusCode().value());
        verify(webAppService, never()).getReportImage(anyInt(), anyString());
    }

    @Test
    void getImageContenuNull() {
        ReportImageResponse meta = mockImageMeta("photo-1", "image/jpeg", "photo.jpg");
        when(webAppService.getReportImages(1)).thenReturn(List.of(meta));
        when(webAppService.getReportImage(1, "photo-1")).thenReturn(null);

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void getImageContenuVide() {
        ReportImageResponse meta = mockImageMeta("photo-1", "image/jpeg", "photo.jpg");
        when(webAppService.getReportImages(1)).thenReturn(List.of(meta));
        when(webAppService.getReportImage(1, "photo-1")).thenReturn(new byte[0]);

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void getImageJpeg() {
        byte[] content = {1, 2, 3};
        ReportImageResponse meta = mockImageMeta("photo-1", "image/jpeg", "photo.jpg");
        when(webAppService.getReportImages(1)).thenReturn(List.of(meta));
        when(webAppService.getReportImage(1, "photo-1")).thenReturn(content);

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertArrayEquals(content, response.getBody());
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION).contains("photo.jpg"));
    }

    @Test
    void getImagePng() {
        ReportImageResponse meta = mockImageMeta("photo-1", "image/png", "photo.png");
        when(webAppService.getReportImages(1)).thenReturn(List.of(meta));
        when(webAppService.getReportImage(1, "photo-1")).thenReturn(new byte[]{1});

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
    }

    @Test
    void getImageGif() {
        ReportImageResponse meta = mockImageMeta("photo-1", "image/gif", "photo.gif");
        when(webAppService.getReportImages(1)).thenReturn(List.of(meta));
        when(webAppService.getReportImage(1, "photo-1")).thenReturn(new byte[]{1});

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.IMAGE_GIF, response.getHeaders().getContentType());
    }

    @Test
    void getImageWebp() {
        ReportImageResponse meta = mockImageMeta("photo-1", "image/webp", "photo.webp");
        when(webAppService.getReportImages(1)).thenReturn(List.of(meta));
        when(webAppService.getReportImage(1, "photo-1")).thenReturn(new byte[]{1});

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("image/webp", response.getHeaders().getContentType().toString());
    }

    @Test
    void getImageContentTypeNullOuVideOuInvalide() {
        for (String badType : new String[]{null, " ", "invalid-content-type"}) {
            ReportImageResponse meta = mockImageMeta("photo-1", badType, "photo.jpg");
            when(webAppService.getReportImages(1)).thenReturn(List.of(meta));
            when(webAppService.getReportImage(1, "photo-1")).thenReturn(new byte[]{1});

            var response = reportController.getReportImage(1, "photo-1");

            assertEquals(200, response.getStatusCode().value());
            assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
        }
    }

    @Test
    void getImageNomFichierNullOuVide() {
        for (String badName : new String[]{null, " "}) {
            ReportImageResponse meta = mockImageMeta("photo-1", "image/jpeg", badName);
            when(webAppService.getReportImages(1)).thenReturn(List.of(meta));
            when(webAppService.getReportImage(1, "photo-1")).thenReturn(new byte[]{1});

            var response = reportController.getReportImage(1, "photo-1");

            assertEquals(200, response.getStatusCode().value());
            assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION).contains("image"));
        }
    }
}