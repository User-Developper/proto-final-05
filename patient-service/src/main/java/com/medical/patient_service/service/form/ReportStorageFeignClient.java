
package com.medical.patient_service.service.form;

import com.medical.patient_service.service.DTO.ReportImageStorageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(
        name = "report-storage-service",
        url = "${services.report-storage.url}"
)
public interface ReportStorageFeignClient {

    @PostMapping(
            value = "/api/reports/{reportId}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    ReportImageStorageResponse uploadImage(
            @PathVariable("reportId") Integer reportId,
            @RequestPart("file") MultipartFile file
    );

    @GetMapping("/api/reports/{reportId}/images")
    List<ReportImageStorageResponse> getImages(
            @PathVariable("reportId") Integer reportId
    );

    @GetMapping("/api/reports/images/{imageId}")
    Resource getImage(
            @PathVariable("imageId") String imageId
    );

    @DeleteMapping("/api/reports/images/{imageId}")
    void deleteImage(
            @PathVariable("imageId") String imageId
    );
}
