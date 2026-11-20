package com.medical.web_app;

import com.medical.web_app.model.Patient;
import com.medical.web_app.model.ProfessionalClient;
import com.medical.web_app.model.User;
import com.medical.web_app.service.PatientserviceFeignClient;
import com.medical.web_app.service.WebAppService;
import com.medical.web_app.service.dto.*;
import com.medical.web_app.service.form.SignupForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebAppServiceTest {

    @Mock
    private PatientserviceFeignClient patientserviceFeignClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private WebAppService webAppService;

    @Test
    void getPatientOk() {
        Patient patient = new Patient();
        when(patientserviceFeignClient.getPatient("test@email.com")).thenReturn(Optional.of(patient));

        Optional<Patient> result = webAppService.getPatient("test@email.com");

        assertTrue(result.isPresent());
        assertSame(patient, result.get());
        verify(patientserviceFeignClient).getPatient("test@email.com");
    }

    @Test
    void getUserOk() {
        User user = new User();
        user.setUsername("testuser");
        when(patientserviceFeignClient.findUser("testuser")).thenReturn(Optional.of(user));

        Optional<User> result = webAppService.getUser("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void getUserIntrouvable() {
        when(patientserviceFeignClient.findUser("unknown")).thenReturn(Optional.empty());

        Optional<User> result = webAppService.getUser("unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserByUsernameOk() {
        User user = new User();
        user.setUsername("testuser");
        when(patientserviceFeignClient.findUser("testuser")).thenReturn(Optional.of(user));

        User result = webAppService.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserByUsernameIntrouvable() {
        when(patientserviceFeignClient.findUser("unknown")).thenReturn(Optional.empty());

        User result = webAppService.getUserByUsername("unknown");

        assertNull(result);
    }

    @Test
    void createUserEncodeMdp() {
        SignupForm form = new SignupForm();
        form.setUsername("testuser");
        form.setPrenom("Jean");
        form.setEmail("test@email.com");
        form.setPassword("password123");

        when(passwordEncoder.encode("password123")).thenReturn("passwordEncoded");
        when(patientserviceFeignClient.addUser(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = webAppService.createUser(form);

        assertEquals("testuser", result.getUsername());
        assertEquals("PATIENT", result.getRole());
        assertEquals("passwordEncoded", result.getPassword());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(patientserviceFeignClient).addUser(captor.capture());
        assertEquals("PATIENT", captor.getValue().getRole());
    }

    @Test
    void setActiveClientOk() {
        User user = new User();
        user.setUsername("testuser");
        when(patientserviceFeignClient.setActiveClient("testuser", 1)).thenReturn(user);

        User result = webAppService.setActiveClient("testuser", 1);

        assertEquals("testuser", result.getUsername());
        verify(patientserviceFeignClient).setActiveClient("testuser", 1);
    }

    @Test
    void submitFinalMappeLesChamps() {
        CreateReportTemp form = new CreateReportTemp();
        form.setTitle("Pollution de l'eau");
        form.setDescription("Pollution constatée");
        form.setLocation("Paris");
        form.setLatitude(48.8566);
        form.setLongitude(2.3522);
        form.setUserId(1L);
        form.setClientId(1);

        ReportResponse response = new ReportResponse();
        when(patientserviceFeignClient.submitFinal(any(CreateReportRequest.class))).thenReturn(response);

        ReportResponse result = webAppService.submitFinal(form);

        assertSame(response, result);
        ArgumentCaptor<CreateReportRequest> captor = ArgumentCaptor.forClass(CreateReportRequest.class);
        verify(patientserviceFeignClient).submitFinal(captor.capture());

        CreateReportRequest req = captor.getValue();
        assertEquals("Pollution de l'eau", req.getTitle());
        assertEquals("Pollution constatée", req.getDescription());
        assertEquals("Paris", req.getLocation());
        assertEquals(48.8566, req.getLatitude());
        assertEquals(2.3522, req.getLongitude());
        assertEquals(1L, req.getUserId());
        assertEquals(1, req.getClientId());
    }

    @Test
    void getAllReportsOk() {
        PageResponse<ReportResponse> response = mock(PageResponse.class);
        when(patientserviceFeignClient.getAllReports(0, 10, null, null, "pollution")).thenReturn(response);

        PageResponse<ReportResponse> result = webAppService.getAllReports(0, 10, null, null, "pollution");

        assertSame(response, result);
    }

    @Test
    void getClientsForUserOk() {
        List<ProfessionalClient> clients = List.of(new ProfessionalClient());
        when(patientserviceFeignClient.getClientsForUser()).thenReturn(clients);

        List<ProfessionalClient> result = webAppService.getClientsForUser(new User());

        assertEquals(1, result.size());
    }

    @Test
    void getReportsByUserOk() {
        ReportSummary report = new ReportSummary();
        report.setTitle("Pollution de l'eau");
        when(patientserviceFeignClient.getReportsByUser(1L)).thenReturn(List.of(report));

        List<ReportSummary> result = webAppService.getReportsByUser(1L);

        assertEquals(1, result.size());
        assertEquals("Pollution de l'eau", result.get(0).getTitle());
    }

    @Test
    void getReportByIdOk() {
        ReportResponse response = new ReportResponse();
        when(patientserviceFeignClient.getReportById(10)).thenReturn(response);

        assertSame(response, webAppService.getReportById(10));
    }

    @Test
    void getClientByIdOk() {
        ProfessionalClient client = new ProfessionalClient();
        when(patientserviceFeignClient.getClientById(1)).thenReturn(client);

        assertSame(client, webAppService.getClientById(1));
    }

    @Test
    void getUserByIdOk() {
        User user = new User();
        user.setUsername("testuser");
        when(patientserviceFeignClient.getUserById(1L)).thenReturn(user);

        User result = webAppService.getUserById(1L);

        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getReportImagesOk() {
        when(patientserviceFeignClient.getReportImages(10)).thenReturn(List.of());

        List<ReportImageResponse> result = webAppService.getReportImages(10);

        assertTrue(result.isEmpty());
    }

    @Test
    void getReportImageOk() {
        byte[] image = {1, 2, 3};
        when(patientserviceFeignClient.getReportImage(10, "image.jpg")).thenReturn(image);

        assertArrayEquals(image, webAppService.getReportImage(10, "image.jpg"));
    }

    @Test
    void uploadReportImageOk() {
        MultipartFile image = new MockMultipartFile("file", "image.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(patientserviceFeignClient.uploadReportImage(10, image)).thenReturn("image.jpg");

        assertEquals("image.jpg", webAppService.uploadReportImage(10, image));
    }

    @Test
    void uploadReportImageNullOuVide() {
        assertNull(webAppService.uploadReportImage(10, null));

        MultipartFile empty = new MockMultipartFile("file", "image.jpg", "image/jpeg", new byte[0]);
        assertNull(webAppService.uploadReportImage(10, empty));

        verify(patientserviceFeignClient, never()).uploadReportImage(anyInt(), any());
    }
}