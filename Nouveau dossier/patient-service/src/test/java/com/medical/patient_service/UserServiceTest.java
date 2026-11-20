package com.medical.patient_service;


import com.medical.patient_service.model.Category;
import com.medical.patient_service.model.ProfessionalClient;
import com.medical.patient_service.model.Report;
import com.medical.patient_service.model.User;
import com.medical.patient_service.service.DTO.CreateReportRequest;
import com.medical.patient_service.service.DTO.ReportResponse;
import com.medical.patient_service.service.DTO.ReportSummary;
import com.medical.patient_service.service.UserService;
import com.medical.patient_service.service.form.DataServiceFeignClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private DataServiceFeignClient dataServiceFeignClient;

    @InjectMocks
    private UserService userService;


    @Test
    void shouldAddUser() {

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@email.com");

        when(dataServiceFeignClient.addUser(user))
                .thenReturn(user);

        User result = userService.addUser(user);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }


    @Test
    void shouldFindUser() {

        User user = new User();
        user.setUsername("testuser");

        when(dataServiceFeignClient.findUser("testuser"))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.findUser("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }


    @Test
    void shouldReturnEmptyWhenUserDoesNotExist() {

        when(dataServiceFeignClient.findUser("unknown"))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.findUser("unknown");

        assertTrue(result.isEmpty());
    }


    @Test
    void shouldGetUserById() {

        User user = new User();
        user.setId(1L);

        when(dataServiceFeignClient.getUserById(1L))
                .thenReturn(user);

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }


    @Test
    void shouldSubmitReport() {

        CreateReportRequest request = new CreateReportRequest();

        ReportResponse response = new ReportResponse();
        response.setId(10);

        when(dataServiceFeignClient.submitFinal(request))
                .thenReturn(response);

        ReportResponse result = userService.submitReportStep1(request);

        assertNotNull(result);
        assertEquals(10, result.getId());
    }


    @Test
    void shouldGetReportById() {

        Report report = new Report();
        report.setId(10);

        when(dataServiceFeignClient.getReportById(10))
                .thenReturn(report);

        Report result = userService.getReportById(10);

        assertNotNull(result);
        assertEquals(10, result.getId());
    }


    @Test
    void shouldGetReportsByUser() {

        Report report = new Report();
        report.setId(10);
        report.setUserId(1L);
        report.setTitle("Pollution de l'eau");
        report.setCategory(Category.WATER_POLLUTION);

        when(dataServiceFeignClient.getReportsByUser(1L))
                .thenReturn(List.of(report));

        List<ReportSummary> result = userService.getReportsByUser(1L);

        assertEquals(1, result.size());
        assertEquals("Pollution de l'eau", result.get(0).getTitle());
        assertEquals(Category.WATER_POLLUTION, result.get(0).getCategory());
    }


    @Test
    void shouldGetClientsForUser() {

        ProfessionalClient client = new ProfessionalClient();
        client.setId(1);
        client.setName("Mairie de Paris");

        when(dataServiceFeignClient.getClientsForUser())
                .thenReturn(List.of(client));

        List<ProfessionalClient> result = userService.getClientsForUser();

        assertEquals(1, result.size());
        assertEquals("Mairie de Paris", result.get(0).getName());
    }


    @Test
    void shouldSetActiveClient() {

        User user = new User();
        user.setUsername("testuser");

        when(dataServiceFeignClient.setActiveClient("testuser", 1))
                .thenReturn(user);

        User result = userService.setActiveClient("testuser", 1);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }


    @Test
    void shouldGetClientById() {

        ProfessionalClient client = new ProfessionalClient();
        client.setId(1);
        client.setName("Mairie de Paris");

        when(dataServiceFeignClient.getClientById(1))
                .thenReturn(client);

        ProfessionalClient result = userService.getClientById(1);

        assertNotNull(result);
        assertEquals("Mairie de Paris", result.getName());
    }
}
