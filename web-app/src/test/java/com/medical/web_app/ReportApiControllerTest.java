package com.medical.web_app;

import com.medical.web_app.controller.ReportApiController;
import com.medical.web_app.model.Category;
import com.medical.web_app.model.Gravity;
import com.medical.web_app.service.WebAppService;
import com.medical.web_app.service.dto.PageResponse;
import com.medical.web_app.service.dto.ReportImageResponse;
import com.medical.web_app.service.dto.ReportResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportApiControllerTest {

    @Mock
    private WebAppService webAppService;

    @InjectMocks
    private ReportApiController reportApiController;

    @Test
    void getReportsOk() {
        PageResponse<ReportResponse> response = mock(PageResponse.class);
        when(webAppService.getAllReports(0, 6, null, null, null)).thenReturn(response);

        PageResponse<ReportResponse> result = reportApiController.getReports(0, 6, null, null, null);

        assertSame(response, result);
        verify(webAppService).getAllReports(0, 6, null, null, null);
    }

    @Test
    void getReportsAvecFiltres() {
        PageResponse<ReportResponse> response = mock(PageResponse.class);
        when(webAppService.getAllReports(1, 10, Category.WASTE, Gravity.HIGH, "déchets"))
                .thenReturn(response);

        PageResponse<ReportResponse> result = reportApiController.getReports(
                1, 10, Category.WASTE, Gravity.HIGH, "déchets"
        );

        assertSame(response, result);
        verify(webAppService).getAllReports(1, 10, Category.WASTE, Gravity.HIGH, "déchets");
    }

    @Test
    void getReportImagesOk() {
        List<ReportImageResponse> images = List.of(new ReportImageResponse());
        when(webAppService.getReportImages(10)).thenReturn(images);

        List<ReportImageResponse> result = reportApiController.getReportImages(10);

        assertEquals(1, result.size());
        assertSame(images, result);
        verify(webAppService).getReportImages(10);
    }

    @Test
    void getReportImagesVide() {
        when(webAppService.getReportImages(10)).thenReturn(List.of());

        List<ReportImageResponse> result = reportApiController.getReportImages(10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(webAppService).getReportImages(10);
    }
}