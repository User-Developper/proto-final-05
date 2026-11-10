package com.medical.patient_service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.medical.patient_service.model.Category;
import com.medical.patient_service.model.ProfessionalClient;
import com.medical.patient_service.model.User;
import com.medical.patient_service.service.DTO.CreateReportRequest;
import com.medical.patient_service.service.DTO.ReportResponse;
import com.medical.patient_service.service.UserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTestIntegration {

    static WireMockServer wireMockServer =
            new WireMockServer(options().dynamicPort());

    static {
        wireMockServer.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.cloud.openfeign.client.config.data-service.url",
                wireMockServer::baseUrl
        );
    }

    @Autowired
    private UserService userService;

    @BeforeEach
    void resetWireMock() {
        wireMockServer.resetAll();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }


    @Test
    void shouldAddUser() {

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@email.com");

        wireMockServer.stubFor(
                post(urlEqualTo("/user"))
                        .willReturn(okJson("""
                                {
                                    "username": "testuser",
                                    "email": "test@email.com"
                                }
                                """))
        );

        User result = userService.addUser(user);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }


    @Test
    void shouldFindUser() {

        wireMockServer.stubFor(
                get(urlPathEqualTo("/user"))
                        .willReturn(okJson("""
                                {
                                    "username": "testuser",
                                    "email": "test@email.com"
                                }
                                """))
        );

        java.util.Optional<User> result = userService.findUser("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }


    @Test
    void shouldSubmitReport() {

        CreateReportRequest request = new CreateReportRequest();

        wireMockServer.stubFor(
                post(urlEqualTo("/reports"))
                        .willReturn(okJson("""
                                {
                                    "id": 10
                                }
                                """))
        );

        ReportResponse result = userService.submitReportStep1(request);

        assertNotNull(result);
        assertEquals(10, result.getId());
    }


    @Test
    void shouldGetReportsByUser() {

        wireMockServer.stubFor(
                get(urlPathEqualTo("/reports"))
                        .willReturn(okJson("""
                                [
                                    {
                                        "id": 10,
                                        "userId": 1,
                                        "title": "Pollution de l'eau",
                                        "description": "Pollution constatée",
                                        "location": "Paris",
                                        "category": "WATER_POLLUTION",
                                        "gravity": "HIGH"
                                    }
                                ]
                                """))
        );

        var result = userService.getReportsByUser(1L);

        assertEquals(1, result.size());
        assertEquals("Pollution de l'eau", result.get(0).getTitle());
        assertEquals(Category.WATER_POLLUTION, result.get(0).getCategory());
    }


    @Test
    void shouldGetClientById() {

        wireMockServer.stubFor(
                get(urlEqualTo("/reports/client/1"))
                        .willReturn(okJson("""
                                {
                                    "id": 1,
                                    "name": "Mairie de Paris"
                                }
                                """))
        );

        ProfessionalClient result = userService.getClientById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Mairie de Paris", result.getName());
    }
}
