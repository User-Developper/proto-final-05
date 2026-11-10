package com.medical.web_app;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.medical.web_app.model.Patient;
import com.medical.web_app.model.ProfessionalClient;
import com.medical.web_app.model.User;
import com.medical.web_app.service.WebAppService;
import com.medical.web_app.service.dto.CreateReportTemp;
import com.medical.web_app.service.dto.ReportImageResponse;
import com.medical.web_app.service.dto.ReportResponse;
import com.medical.web_app.service.dto.ReportSummary;
import com.medical.web_app.service.form.SignupForm;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext
class WebAppServiceIntegrationTest {

    private static final WireMockServer wireMockServer =
            new WireMockServer(wireMockConfig().dynamicPort());

    static {
        wireMockServer.start();
    }

    @Autowired
    private WebAppService webAppService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

        registry.add(
                "spring.cloud.openfeign.client.config.patient-service.url",
                wireMockServer::baseUrl
        );

        registry.add("spring.cloud.config.enabled", () -> false);
        registry.add("spring.cloud.consul.enabled", () -> false);
        registry.add("spring.cloud.discovery.enabled", () -> false);
    }


    @BeforeEach
    void resetWireMock() {
        wireMockServer.resetAll();
    }


    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }


    @Test
    void shouldGetPatientThroughFeign() {

        wireMockServer.stubFor(
                get(urlPathEqualTo("/patients"))
                        .withQueryParam("email", equalTo("test@email.com"))
                        .willReturn(okJson("""
                                {
                                    "email": "test@email.com"
                                }
                                """))
        );

        Optional<Patient> result =
                webAppService.getPatient("test@email.com");

        assertTrue(result.isPresent());

        wireMockServer.verify(
                getRequestedFor(urlPathEqualTo("/patients"))
                        .withQueryParam("email", equalTo("test@email.com"))
        );
    }


    @Test
    void shouldGetUserThroughFeign() {

        wireMockServer.stubFor(
                get(urlPathEqualTo("/user"))
                        .withQueryParam("username", equalTo("testuser"))
                        .willReturn(okJson("""
                                {
                                    "username": "testuser",
                                    "prenom": "Jean",
                                    "email": "test@email.com",
                                    "role": "PATIENT"
                                }
                                """))
        );

        Optional<User> result =
                webAppService.getUser("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("Jean", result.get().getPrenom());

        wireMockServer.verify(
                getRequestedFor(urlPathEqualTo("/user"))
                        .withQueryParam("username", equalTo("testuser"))
        );
    }


    @Test
    void shouldCreateUserThroughFeign() {

        wireMockServer.stubFor(
                post(urlEqualTo("/user"))
                        .withRequestBody(matchingJsonPath("$.username",
                                equalTo("testuser")))
                        .withRequestBody(matchingJsonPath("$.role",
                                equalTo("PATIENT")))
                        .willReturn(okJson("""
                                {
                                    "username": "testuser",
                                    "prenom": "Jean",
                                    "email": "test@email.com",
                                    "role": "PATIENT"
                                }
                                """))
        );

        SignupForm form = new SignupForm();
        form.setUsername("testuser");
        form.setPrenom("Jean");
        form.setEmail("test@email.com");
        form.setPassword("password123");

        User result = webAppService.createUser(form);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("PATIENT", result.getRole());

        wireMockServer.verify(
                postRequestedFor(urlEqualTo("/user"))
                        .withRequestBody(matchingJsonPath("$.username",
                                equalTo("testuser")))
                        .withRequestBody(matchingJsonPath("$.password",
                                matching(".+")))
        );
    }


    @Test
    void shouldSetActiveClientThroughFeign() {

        wireMockServer.stubFor(
                post(urlEqualTo(
                        "/client-selector/users/testuser/active-client/1"))
                        .willReturn(okJson("""
                                {
                                    "username": "testuser"
                                }
                                """))
        );

        User result =
                webAppService.setActiveClient("testuser", 1);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());

        wireMockServer.verify(
                postRequestedFor(urlEqualTo(
                        "/client-selector/users/testuser/active-client/1"))
        );
    }


    @Test
    void shouldSubmitReportThroughFeign() {

        wireMockServer.stubFor(
                post(urlEqualTo("/reports"))
                        .withRequestBody(matchingJsonPath("$.title",
                                equalTo("Pollution de l'eau")))
                        .withRequestBody(matchingJsonPath("$.userId",
                                equalTo("1")))
                        .willReturn(okJson("""
                                {
                                    "id": 10,
                                    "title": "Pollution de l'eau"
                                }
                                """))
        );

        CreateReportTemp form = new CreateReportTemp();
        form.setTitle("Pollution de l'eau");
        form.setDescription("Pollution constatée");
        form.setLocation("Paris");
        form.setUserId(1L);
        form.setClientId(1);

        ReportResponse result = webAppService.submitFinal(form);

        assertNotNull(result);

        wireMockServer.verify(
                postRequestedFor(urlEqualTo("/reports"))
                        .withRequestBody(matchingJsonPath("$.title",
                                equalTo("Pollution de l'eau")))
                        .withRequestBody(matchingJsonPath("$.location",
                                equalTo("Paris")))
        );
    }


    @Test
    void shouldGetReportsByUserThroughFeign() {

        wireMockServer.stubFor(
                get(urlPathEqualTo("/reports"))
                        .withQueryParam("userId", equalTo("1"))
                        .willReturn(okJson("""
                                [
                                    {
                                        "id": 10,
                                        "title": "Pollution de l'eau"
                                    }
                                ]
                                """))
        );

        List<ReportSummary> result =
                webAppService.getReportsByUser(1L);

        assertEquals(1, result.size());
        assertEquals("Pollution de l'eau", result.get(0).getTitle());

        wireMockServer.verify(
                getRequestedFor(urlPathEqualTo("/reports"))
                        .withQueryParam("userId", equalTo("1"))
        );
    }


    @Test
    void shouldGetReportByIdThroughFeign() {

        wireMockServer.stubFor(
                get(urlEqualTo("/reports/10"))
                        .willReturn(okJson("""
                                {
                                    "id": 10,
                                    "title": "Pollution de l'eau"
                                }
                                """))
        );

        ReportResponse result = webAppService.getReportById(10);

        assertNotNull(result);

        wireMockServer.verify(
                getRequestedFor(urlEqualTo("/reports/10"))
        );
    }


    @Test
    void shouldGetUserByIdThroughFeign() {

        wireMockServer.stubFor(
                get(urlEqualTo("/user/1"))
                        .willReturn(okJson("""
                                {
                                    "username": "testuser",
                                    "prenom": "Jean"
                                }
                                """))
        );

        User result = webAppService.getUserById(1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());

        wireMockServer.verify(
                getRequestedFor(urlEqualTo("/user/1"))
        );
    }


    @Test
    void shouldGetClientsForUserThroughFeign() {

        wireMockServer.stubFor(
                get(urlEqualTo("/client-selector"))
                        .willReturn(okJson("[]"))
        );

        List<ProfessionalClient> result =
                webAppService.getClientsForUser(new User());

        assertNotNull(result);
        assertTrue(result.isEmpty());

        wireMockServer.verify(
                getRequestedFor(urlEqualTo("/client-selector"))
        );
    }


    @Test
    void shouldGetClientByIdThroughFeign() {

        wireMockServer.stubFor(
                get(urlEqualTo("/reports/client/1"))
                        .willReturn(okJson("{}"))
        );

        ProfessionalClient result =
                webAppService.getClientById(1);

        assertNotNull(result);

        wireMockServer.verify(
                getRequestedFor(urlEqualTo("/reports/client/1"))
        );
    }


    @Test
    void shouldGetReportImagesThroughFeign() {

        wireMockServer.stubFor(
                get(urlEqualTo("/reports/10/images"))
                        .willReturn(okJson("[]"))
        );

        List<ReportImageResponse> result =
                webAppService.getReportImages(10);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        wireMockServer.verify(
                getRequestedFor(urlEqualTo("/reports/10/images"))
        );
    }


    @Test
    void shouldGetReportImageThroughFeign() {

        byte[] imageContent = {1, 2, 3, 4};

        wireMockServer.stubFor(
                get(urlEqualTo("/reports/10/images/image.jpg"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "image/jpeg")
                                        .withBody(imageContent)
                        )
        );

        byte[] result =
                webAppService.getReportImage(10, "image.jpg");

        assertArrayEquals(imageContent, result);

        wireMockServer.verify(
                getRequestedFor(
                        urlEqualTo("/reports/10/images/image.jpg")
                )
        );
    }


    @Test
    void shouldUploadReportImageThroughFeign() {

        wireMockServer.stubFor(
                post(urlEqualTo("/reports/10/images"))
                        .withRequestBody(containing("image.jpg"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "text/plain")
                                        .withBody("image.jpg")
                        )
        );

        MockMultipartFile image = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3, 4}
        );

        String result = webAppService.uploadReportImage(10, image);

        assertEquals("image.jpg", result);

        wireMockServer.verify(
                postRequestedFor(urlEqualTo("/reports/10/images"))
                        .withRequestBody(containing("image.jpg"))
        );
    }
}