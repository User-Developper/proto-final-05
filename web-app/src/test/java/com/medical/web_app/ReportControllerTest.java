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
    void setUp() {
        model = new ExtendedModelMap();
        session = new MockHttpSession();
        principal = () -> "alice";
    }

    private User createUser() {
        User user = new User();
        user.setId(10L);
        user.setUsername("alice");
        return user;
    }

    private CreateReportTemp createReport() {
        CreateReportTemp report = new CreateReportTemp();
        report.setUserId(10L);
        report.setTitle("Test report");
        report.setDescription("Description test");
        report.setLocation("Paris");
        report.setClientId(5);
        return report;
    }

    private ReportResponse createReportResponse() {
        ReportResponse response = new ReportResponse();
        response.setId(1);
        response.setUserId(10L);
        return response;
    }

    private ReportImageResponse createImageMetadata(
            String id,
            String contentType,
            String fileName
    ) {
        ReportImageResponse metadata = mock(ReportImageResponse.class);

        when(metadata.getId()).thenReturn(id);
        lenient().when(metadata.getContentType()).thenReturn(contentType);
        lenient().when(metadata.getFileName()).thenReturn(fileName);

        return metadata;
    }

    @Test
    void shouldReturnReportPage() {
        ReportResponse report = createReportResponse();
        User author = createUser();

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
    void shouldRedirectWhenReportDoesNotExist() {
        when(webAppService.getReportById(1)).thenReturn(null);

        String view = reportController.reportPage(1, model);

        assertEquals("redirect:/report-list", view);
    }

    @Test
    void shouldReturnReportPageWhenAuthorDoesNotExist() {
        ReportResponse report = createReportResponse();

        when(webAppService.getReportById(1)).thenReturn(report);
        when(webAppService.getUserById(10L)).thenReturn(null);
        when(webAppService.getReportImages(1)).thenReturn(List.of());

        String view = reportController.reportPage(1, model);

        assertEquals("report", view);
        assertNull(model.getAttribute("author"));
    }

    @Test
    void shouldUseEmptyImageListWhenImagesAreNull() {
        ReportResponse report = createReportResponse();

        when(webAppService.getReportById(1)).thenReturn(report);
        when(webAppService.getUserById(10L)).thenReturn(createUser());
        when(webAppService.getReportImages(1)).thenReturn(null);

        String view = reportController.reportPage(1, model);

        assertEquals("report", view);
        assertEquals(List.of(), model.getAttribute("reportImages"));
    }

    @Test
    void shouldReturnCreateReportPage() {
        String view = reportController.createReport(model);

        assertEquals("create-report", view);
        assertNotNull(model.getAttribute("reportForm"));
    }

    @Test
    void shouldRedirectToSigninWhenUserDoesNotExist() {
        ReportForm form = new ReportForm();

        when(webAppService.getUser("alice")).thenReturn(Optional.empty());

        String view = reportController.submitReportStep1(
                form,
                null,
                principal,
                session
        );

        assertEquals("redirect:/signin", view);
    }

    @Test
    void shouldSubmitReportStep1WithoutImages() {
        User user = createUser();
        ReportForm form = new ReportForm();

        when(webAppService.getUser("alice")).thenReturn(Optional.of(user));

        String view = reportController.submitReportStep1(
                form,
                null,
                principal,
                session
        );

        assertEquals("redirect:/create-report-step-0", view);
        assertNotNull(session.getAttribute("report"));
        assertEquals(List.of(), session.getAttribute("reportImageBytes"));
        assertEquals(List.of(), session.getAttribute("reportImageTypes"));
        assertEquals(List.of(), session.getAttribute("reportImageNames"));
    }

    @Test
    void shouldSubmitReportStep1WithImages() {
        User user = createUser();
        ReportForm form = new ReportForm();

        MockMultipartFile image = new MockMultipartFile(
                "images",
                "photo.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3}
        );

        when(webAppService.getUser("alice")).thenReturn(Optional.of(user));

        String view = reportController.submitReportStep1(
                form,
                List.of(image),
                principal,
                session
        );

        assertEquals("redirect:/create-report-step-0", view);

        List<byte[]> images =
                (List<byte[]>) session.getAttribute("reportImageBytes");

        assertEquals(1, images.size());
        assertArrayEquals(new byte[]{1, 2, 3}, images.get(0));
    }

    @Test
    void shouldIgnoreEmptyImages() {
        User user = createUser();
        ReportForm form = new ReportForm();

        MockMultipartFile emptyImage = new MockMultipartFile(
                "images",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        when(webAppService.getUser("alice")).thenReturn(Optional.of(user));

        String view = reportController.submitReportStep1(
                form,
                List.of(emptyImage),
                principal,
                session
        );

        assertEquals("redirect:/create-report-step-0", view);

        List<byte[]> images =
                (List<byte[]>) session.getAttribute("reportImageBytes");

        assertTrue(images.isEmpty());
    }

    @Test
    void shouldRedirectToSigninWhenUserDoesNotExistAtStep2() {
        when(webAppService.getUser("alice")).thenReturn(Optional.empty());

        String view = reportController.submitReportStep2(
                model,
                session,
                principal
        );

        assertEquals("redirect:/signin", view);
    }

    @Test
    void shouldRedirectToCreateReportWhenTemporaryReportDoesNotExistAtStep2() {
        when(webAppService.getUser("alice"))
                .thenReturn(Optional.of(createUser()));

        String view = reportController.submitReportStep2(
                model,
                session,
                principal
        );

        assertEquals("redirect:/create-report", view);
    }

    @Test
    void shouldReturnStep2Page() {
        User user = createUser();
        session.setAttribute("report", createReport());

        when(webAppService.getUser("alice")).thenReturn(Optional.of(user));

        String view = reportController.submitReportStep2(
                model,
                session,
                principal
        );

        assertEquals("create-report-step-0", view);
        assertEquals(user, model.getAttribute("user"));
    }

    @Test
    void shouldRedirectWhenReportIsNullAtStep2Submission() {
        String view = reportController.submitStep2(
                48.8566,
                2.3522,
                "Paris",
                5,
                session
        );

        assertEquals("redirect:/create-report", view);
    }

    @Test
    void shouldSubmitStep2() {
        CreateReportTemp report = createReport();
        session.setAttribute("report", report);

        String view = reportController.submitStep2(
                48.8566,
                2.3522,
                "Paris",
                5,
                session
        );

        assertEquals("redirect:/create-report-step-1", view);
        assertEquals(48.8566, report.getLatitude());
        assertEquals(2.3522, report.getLongitude());
        assertEquals("Paris", report.getLocation());
        assertEquals(5, report.getClientId());
    }

    @Test
    void shouldRedirectWhenReportIsNullAtStep3() {
        String view = reportController.submitReportStep3(model, session);

        assertEquals("redirect:/create-report", view);
    }

    @Test
    void shouldReturnStep3Page() {
        CreateReportTemp report = createReport();
        session.setAttribute("report", report);

        when(webAppService.getClientById(5)).thenReturn(null);

        String view = reportController.submitReportStep3(model, session);

        assertEquals("create-report-step-1", view);
        assertEquals(report, model.getAttribute("report"));
        assertNull(model.getAttribute("client"));
        assertEquals(List.of(), model.getAttribute("reportImages"));
    }

    @Test
    void shouldDisplayTemporaryImagesAtStep3() {
        CreateReportTemp report = createReport();

        session.setAttribute("report", report);
        session.setAttribute("reportImageBytes", List.of(new byte[]{1, 2, 3}));
        session.setAttribute("reportImageTypes", List.of("image/jpeg"));

        when(webAppService.getClientById(5)).thenReturn(null);

        String view = reportController.submitReportStep3(model, session);

        assertEquals("create-report-step-1", view);

        List<String> images =
                (List<String>) model.getAttribute("reportImages");

        assertEquals(1, images.size());
        assertTrue(images.get(0).startsWith("data:image/jpeg;base64,"));
    }

    @Test
    void shouldIgnoreTemporaryImagesWhenTypesAreNull() {
        session.setAttribute("report", createReport());
        session.setAttribute("reportImageBytes", List.of(new byte[]{1, 2, 3}));

        when(webAppService.getClientById(5)).thenReturn(null);

        String view = reportController.submitReportStep3(model, session);

        assertEquals("create-report-step-1", view);
        assertEquals(List.of(), model.getAttribute("reportImages"));
    }

    @Test
    void shouldIgnoreTemporaryImagesWithNullBytesOrType() {
        session.setAttribute("report", createReport());
        session.setAttribute("reportImageBytes",
                Collections.singletonList((byte[]) null));
        session.setAttribute("reportImageTypes",
                Collections.singletonList((String) null));

        when(webAppService.getClientById(5)).thenReturn(null);

        String view = reportController.submitReportStep3(model, session);

        assertEquals("create-report-step-1", view);
        assertEquals(List.of(), model.getAttribute("reportImages"));
    }

    @Test
    void shouldRedirectWhenReportIsNullAtFinalSubmission() {
        String view = reportController.finalSubmit(
                session,
                new RedirectAttributesModelMap()
        );

        assertEquals("redirect:/create-report", view);
    }

    @Test
    void shouldSubmitFinalReportWithoutImages() {
        CreateReportTemp report = createReport();
        session.setAttribute("report", report);

        ReportResponse response = createReportResponse();

        when(webAppService.submitFinal(report)).thenReturn(response);

        String view = reportController.finalSubmit(
                session,
                new RedirectAttributesModelMap()
        );

        assertEquals("redirect:/report-success", view);
        verify(webAppService).submitFinal(report);
        assertNull(session.getAttribute("report"));
    }

    @Test
    void shouldSubmitFinalReportWithImages() {
        CreateReportTemp report = createReport();

        session.setAttribute("report", report);
        session.setAttribute("reportImageBytes", List.of(new byte[]{1, 2, 3}));
        session.setAttribute("reportImageTypes", List.of("image/jpeg"));
        session.setAttribute("reportImageNames", List.of("photo.jpg"));

        ReportResponse response = createReportResponse();

        when(webAppService.submitFinal(report)).thenReturn(response);
        when(webAppService.uploadReportImage(eq(1), any(MultipartFile.class)))
                .thenReturn("stored-photo.jpg");

        String view = reportController.finalSubmit(
                session,
                new RedirectAttributesModelMap()
        );

        assertEquals("redirect:/report-success", view);

        verify(webAppService).submitFinal(report);
        verify(webAppService).uploadReportImage(
                eq(1),
                any(MultipartFile.class)
        );

        assertNull(session.getAttribute("report"));
        assertNull(session.getAttribute("reportImageBytes"));
        assertNull(session.getAttribute("reportImageTypes"));
        assertNull(session.getAttribute("reportImageNames"));
    }

    @Test
    void shouldUseDefaultFilenameWhenOriginalFilenameIsMissing() {
        CreateReportTemp report = createReport();

        session.setAttribute("report", report);
        session.setAttribute("reportImageBytes", List.of(new byte[]{1, 2, 3}));
        session.setAttribute("reportImageTypes", List.of("image/jpeg"));
        session.setAttribute("reportImageNames", List.of(" "));

        when(webAppService.submitFinal(report))
                .thenReturn(createReportResponse());

        when(webAppService.uploadReportImage(eq(1), any(MultipartFile.class)))
                .thenReturn("stored-photo.jpg");

        String view = reportController.finalSubmit(
                session,
                new RedirectAttributesModelMap()
        );

        assertEquals("redirect:/report-success", view);

        verify(webAppService).uploadReportImage(
                eq(1),
                argThat(file -> "image-1".equals(file.getOriginalFilename()))
        );
    }

    @Test
    void shouldIgnoreEmptyImageBytesDuringFinalSubmission() {
        CreateReportTemp report = createReport();

        session.setAttribute("report", report);
        session.setAttribute("reportImageBytes", List.of(new byte[0]));
        session.setAttribute("reportImageTypes", List.of("image/jpeg"));

        when(webAppService.submitFinal(report))
                .thenReturn(createReportResponse());

        String view = reportController.finalSubmit(
                session,
                new RedirectAttributesModelMap()
        );

        assertEquals("redirect:/report-success", view);

        verify(webAppService, never())
                .uploadReportImage(anyInt(), any(MultipartFile.class));
    }

    @Test
    void shouldReturnReportSuccessPage() {
        assertEquals("report-success", reportController.reportSuccess());
    }

    @Test
    void shouldReturnReportListPage() {
        assertEquals("report-list", reportController.reportList());
    }

    @Test
    void shouldReturnConditionsPage() {
        assertEquals(
                "conditions-utilisation",
                reportController.conditionsUtilisation()
        );
    }

    @Test
    void shouldReturnPrivacyPolicyPage() {
        assertEquals(
                "politique-confidentialite",
                reportController.politiqueConfidentialite()
        );
    }

    @Test
    void shouldReturnNotFoundWhenImageListIsNull() {
        when(webAppService.getReportImages(1)).thenReturn(null);

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(404, response.getStatusCode().value());

        verify(webAppService, never())
                .getReportImage(anyInt(), anyString());
    }

    @Test
    void shouldReturnNotFoundWhenImageDoesNotExistInList() {
        ReportImageResponse metadata =
                createImageMetadata("photo-2", "image/jpeg", "photo.jpg");

        when(webAppService.getReportImages(1))
                .thenReturn(List.of(metadata));

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(404, response.getStatusCode().value());

        verify(webAppService, never())
                .getReportImage(anyInt(), anyString());
    }

    @Test
    void shouldReturnNotFoundWhenImageDataIsNull() {
        ReportImageResponse metadata =
                createImageMetadata("photo-1", "image/jpeg", "photo.jpg");

        when(webAppService.getReportImages(1))
                .thenReturn(List.of(metadata));

        when(webAppService.getReportImage(1, "photo-1"))
                .thenReturn(null);

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void shouldReturnNotFoundWhenImageDataIsEmpty() {
        ReportImageResponse metadata =
                createImageMetadata("photo-1", "image/jpeg", "photo.jpg");

        when(webAppService.getReportImages(1))
                .thenReturn(List.of(metadata));

        when(webAppService.getReportImage(1, "photo-1"))
                .thenReturn(new byte[0]);

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void shouldReturnJpegImage() {
        byte[] image = {1, 2, 3};

        ReportImageResponse metadata =
                createImageMetadata("photo-1", "image/jpeg", "photo.jpg");

        when(webAppService.getReportImages(1))
                .thenReturn(List.of(metadata));

        when(webAppService.getReportImage(1, "photo-1"))
                .thenReturn(image);

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertArrayEquals(image, response.getBody());

        assertTrue(response.getHeaders()
                .getFirst(HttpHeaders.CONTENT_DISPOSITION)
                .contains("photo.jpg"));
    }

    @Test
    void shouldReturnPngImage() {
        ReportImageResponse metadata =
                createImageMetadata("photo-1", "image/png", "photo.png");

        when(webAppService.getReportImages(1))
                .thenReturn(List.of(metadata));

        when(webAppService.getReportImage(1, "photo-1"))
                .thenReturn(new byte[]{1});

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
    }

    @Test
    void shouldReturnGifImage() {
        ReportImageResponse metadata =
                createImageMetadata("photo-1", "image/gif", "photo.gif");

        when(webAppService.getReportImages(1))
                .thenReturn(List.of(metadata));

        when(webAppService.getReportImage(1, "photo-1"))
                .thenReturn(new byte[]{1});

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.IMAGE_GIF, response.getHeaders().getContentType());
    }

    @Test
    void shouldReturnWebpImage() {
        ReportImageResponse metadata =
                createImageMetadata("photo-1", "image/webp", "photo.webp");

        when(webAppService.getReportImages(1))
                .thenReturn(List.of(metadata));

        when(webAppService.getReportImage(1, "photo-1"))
                .thenReturn(new byte[]{1});

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(
                "image/webp",
                response.getHeaders().getContentType().toString()
        );
    }

    @Test
    void shouldUseDefaultContentTypeWhenContentTypeIsNull() {
        ReportImageResponse metadata =
                createImageMetadata("photo-1", null, "photo.jpg");

        when(webAppService.getReportImages(1))
                .thenReturn(List.of(metadata));

        when(webAppService.getReportImage(1, "photo-1"))
                .thenReturn(new byte[]{1});

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(
                MediaType.APPLICATION_OCTET_STREAM,
                response.getHeaders().getContentType()
        );
    }

    @Test
    void shouldUseDefaultContentTypeWhenContentTypeIsBlank() {
        ReportImageResponse metadata =
                createImageMetadata("photo-1", " ", "photo.jpg");

        when(webAppService.getReportImages(1))
                .thenReturn(List.of(metadata));

        when(webAppService.getReportImage(1, "photo-1"))
                .thenReturn(new byte[]{1});

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(
                MediaType.APPLICATION_OCTET_STREAM,
                response.getHeaders().getContentType()
        );
    }

    @Test
    void shouldUseDefaultContentTypeWhenContentTypeIsInvalid() {
        ReportImageResponse metadata =
                createImageMetadata(
                        "photo-1",
                        "invalid-content-type",
                        "photo.jpg"
                );

        when(webAppService.getReportImages(1))
                .thenReturn(List.of(metadata));

        when(webAppService.getReportImage(1, "photo-1"))
                .thenReturn(new byte[]{1});

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(
                MediaType.APPLICATION_OCTET_STREAM,
                response.getHeaders().getContentType()
        );
    }

    @Test
    void shouldUseDefaultFilenameWhenFilenameIsNull() {
        ReportImageResponse metadata =
                createImageMetadata("photo-1", "image/jpeg", null);

        when(webAppService.getReportImages(1))
                .thenReturn(List.of(metadata));

        when(webAppService.getReportImage(1, "photo-1"))
                .thenReturn(new byte[]{1});

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(200, response.getStatusCode().value());

        assertTrue(response.getHeaders()
                .getFirst(HttpHeaders.CONTENT_DISPOSITION)
                .contains("image"));
    }

    @Test
    void shouldUseDefaultFilenameWhenFilenameIsBlank() {
        ReportImageResponse metadata =
                createImageMetadata("photo-1", "image/jpeg", " ");

        when(webAppService.getReportImages(1))
                .thenReturn(List.of(metadata));

        when(webAppService.getReportImage(1, "photo-1"))
                .thenReturn(new byte[]{1});

        var response = reportController.getReportImage(1, "photo-1");

        assertEquals(200, response.getStatusCode().value());

        assertTrue(response.getHeaders()
                .getFirst(HttpHeaders.CONTENT_DISPOSITION)
                .contains("image"));
    }
}
