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
    void shouldGetPatient() {

        Patient patient = new Patient();

        when(patientserviceFeignClient.getPatient("test@email.com"))
                .thenReturn(Optional.of(patient));

        Optional<Patient> result =
                webAppService.getPatient("test@email.com");

        assertTrue(result.isPresent());
        assertSame(patient, result.get());

        verify(patientserviceFeignClient)
                .getPatient("test@email.com");
    }


    @Test
    void shouldGetUser() {

        User user = new User();
        user.setUsername("testuser");

        when(patientserviceFeignClient.findUser("testuser"))
                .thenReturn(Optional.of(user));

        Optional<User> result = webAppService.getUser("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());

        verify(patientserviceFeignClient).findUser("testuser");
    }


    @Test
    void shouldReturnEmptyWhenUserDoesNotExist() {

        when(patientserviceFeignClient.findUser("unknown"))
                .thenReturn(Optional.empty());

        Optional<User> result = webAppService.getUser("unknown");

        assertTrue(result.isEmpty());

        verify(patientserviceFeignClient).findUser("unknown");
    }


    @Test
    void shouldGetUserByUsername() {

        User user = new User();
        user.setUsername("testuser");

        when(patientserviceFeignClient.findUser("testuser"))
                .thenReturn(Optional.of(user));

        User result = webAppService.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }


    @Test
    void shouldReturnNullWhenUserByUsernameDoesNotExist() {

        when(patientserviceFeignClient.findUser("unknown"))
                .thenReturn(Optional.empty());

        User result = webAppService.getUserByUsername("unknown");

        assertNull(result);
    }


    @Test
    void shouldCreateUser() {

        SignupForm form = new SignupForm();
        form.setUsername("testuser");
        form.setPrenom("Jean");
        form.setEmail("test@email.com");
        form.setPassword("password123");

        when(passwordEncoder.encode("password123"))
                .thenReturn("passwordEncoded");

        when(patientserviceFeignClient.addUser(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = webAppService.createUser(form);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("Jean", result.getPrenom());
        assertEquals("test@email.com", result.getEmail());
        assertEquals("PATIENT", result.getRole());
        assertEquals("passwordEncoded", result.getPassword());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(patientserviceFeignClient).addUser(captor.capture());

        assertEquals("testuser", captor.getValue().getUsername());
        assertEquals("PATIENT", captor.getValue().getRole());

        verify(passwordEncoder).encode("password123");
    }


    @Test
    void shouldSetActiveClient() {

        User user = new User();
        user.setUsername("testuser");

        when(patientserviceFeignClient.setActiveClient("testuser", 1))
                .thenReturn(user);

        User result = webAppService.setActiveClient("testuser", 1);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());

        verify(patientserviceFeignClient).setActiveClient("testuser", 1);
    }


    @Test
    void shouldSubmitFinalReport() {

        CreateReportTemp form = new CreateReportTemp();
        form.setTitle("Pollution de l'eau");
        form.setDescription("Pollution constatée");
        form.setLocation("Paris");
        form.setLatitude(48.8566);
        form.setLongitude(2.3522);
        form.setUserId(1L);
        form.setClientId(1);

        ReportResponse response = new ReportResponse();

        when(patientserviceFeignClient.submitFinal(any(CreateReportRequest.class)))
                .thenReturn(response);

        ReportResponse result = webAppService.submitFinal(form);

        assertSame(response, result);

        ArgumentCaptor<CreateReportRequest> captor =
                ArgumentCaptor.forClass(CreateReportRequest.class);

        verify(patientserviceFeignClient).submitFinal(captor.capture());

        CreateReportRequest request = captor.getValue();

        assertEquals("Pollution de l'eau", request.getTitle());
        assertEquals("Pollution constatée", request.getDescription());
        assertEquals("Paris", request.getLocation());
        assertEquals(48.8566, request.getLatitude());
        assertEquals(2.3522, request.getLongitude());
        assertEquals(1L, request.getUserId());
        assertEquals(1, request.getClientId());
    }


    @Test
    void shouldGetAllReports() {

        PageResponse<ReportResponse> response = mock(PageResponse.class);

        when(patientserviceFeignClient.getAllReports(
                0, 10, null, null, "pollution"))
                .thenReturn(response);

        PageResponse<ReportResponse> result =
                webAppService.getAllReports(0, 10, null, null, "pollution");

        assertSame(response, result);

        verify(patientserviceFeignClient)
                .getAllReports(0, 10, null, null, "pollution");
    }


    @Test
    void shouldGetClientsForUser() {

        User user = new User();
        List<ProfessionalClient> clients =
                List.of(new ProfessionalClient());

        when(patientserviceFeignClient.getClientsForUser())
                .thenReturn(clients);

        List<ProfessionalClient> result =
                webAppService.getClientsForUser(user);

        assertEquals(1, result.size());
        assertSame(clients.get(0), result.get(0));

        verify(patientserviceFeignClient).getClientsForUser();
    }


    @Test
    void shouldGetReportsByUser() {

        ReportSummary report = new ReportSummary();
        report.setTitle("Pollution de l'eau");

        when(patientserviceFeignClient.getReportsByUser(1L))
                .thenReturn(List.of(report));

        List<ReportSummary> result =
                webAppService.getReportsByUser(1L);

        assertEquals(1, result.size());
        assertEquals("Pollution de l'eau", result.get(0).getTitle());

        verify(patientserviceFeignClient).getReportsByUser(1L);
    }


    @Test
    void shouldGetReportById() {

        ReportResponse response = new ReportResponse();

        when(patientserviceFeignClient.getReportById(10))
                .thenReturn(response);

        ReportResponse result = webAppService.getReportById(10);

        assertSame(response, result);

        verify(patientserviceFeignClient).getReportById(10);
    }


    @Test
    void shouldGetClientById() {

        ProfessionalClient client = new ProfessionalClient();

        when(patientserviceFeignClient.getClientById(1))
                .thenReturn(client);

        ProfessionalClient result = webAppService.getClientById(1);

        assertSame(client, result);

        verify(patientserviceFeignClient).getClientById(1);
    }


    @Test
    void shouldGetUserById() {

        User user = new User();
        user.setUsername("testuser");

        when(patientserviceFeignClient.getUserById(1L))
                .thenReturn(user);

        User result = webAppService.getUserById(1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());

        verify(patientserviceFeignClient).getUserById(1L);
    }


    @Test
    void shouldGetReportImages() {

        List<ReportImageResponse> images = List.of();

        when(patientserviceFeignClient.getReportImages(10))
                .thenReturn(images);

        List<ReportImageResponse> result =
                webAppService.getReportImages(10);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(patientserviceFeignClient).getReportImages(10);
    }


    @Test
    void shouldGetReportImage() {

        byte[] image = {1, 2, 3};

        when(patientserviceFeignClient.getReportImage(10, "image.jpg"))
                .thenReturn(image);

        byte[] result =
                webAppService.getReportImage(10, "image.jpg");

        assertArrayEquals(image, result);

        verify(patientserviceFeignClient)
                .getReportImage(10, "image.jpg");
    }


    @Test
    void shouldUploadReportImage() {

        MultipartFile image = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3}
        );

        when(patientserviceFeignClient.uploadReportImage(10, image))
                .thenReturn("image.jpg");

        String result = webAppService.uploadReportImage(10, image);

        assertEquals("image.jpg", result);

        verify(patientserviceFeignClient)
                .uploadReportImage(10, image);
    }


    @Test
    void shouldReturnNullWhenImageIsNull() {

        String result = webAppService.uploadReportImage(10, null);

        assertNull(result);

        verify(patientserviceFeignClient, never())
                .uploadReportImage(anyInt(), any());
    }


    @Test
    void shouldReturnNullWhenImageIsEmpty() {

        MultipartFile image = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                new byte[0]
        );

        String result = webAppService.uploadReportImage(10, image);

        assertNull(result);

        verify(patientserviceFeignClient, never())
                .uploadReportImage(anyInt(), any());
    }
}