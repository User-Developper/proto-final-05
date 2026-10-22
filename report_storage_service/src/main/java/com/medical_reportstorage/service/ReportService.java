
package com.medical_reportstorage.service;

import com.medical_reportstorage.model.ReportDocument;
import com.medical_reportstorage.repository.ReportRepository;
import com.mongodb.client.gridfs.GridFSBucket;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final GridFsTemplate gridFsTemplate;
    private final GridFSBucket gridFSBucket;
    private final MongoTemplate mongoTemplate;

    public ReportService(
            ReportRepository reportRepository,
            GridFsTemplate gridFsTemplate,
            GridFSBucket gridFSBucket,
            MongoTemplate mongoTemplate) {
        this.reportRepository = reportRepository;
        this.gridFsTemplate = gridFsTemplate;
        this.gridFSBucket = gridFSBucket;
        this.mongoTemplate = mongoTemplate;
    }

    public ReportDocument storeImage(
            Integer reportId,
            MultipartFile file) throws IOException {

        if (reportId == null) {
            throw new IllegalArgumentException(
                    "L'identifiant du signalement est obligatoire.");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Le fichier est vide.");
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !contentType.toLowerCase().startsWith("image/")) {
            throw new IllegalArgumentException(
                    "Le fichier doit être une image.");
        }

        String originalFilename = file.getOriginalFilename();

        ObjectId fileId;

        try (var inputStream = file.getInputStream()) {
            fileId = gridFsTemplate.store(
                    inputStream,
                    originalFilename != null
                            ? originalFilename
                            : "image",
                    contentType
            );
        }

        ReportDocument document = new ReportDocument(
                reportId,
                originalFilename,
                contentType,
                fileId.toHexString(),
                file.getSize(),
                LocalDateTime.now()
        );

        try {
            return reportRepository.save(document);
        } catch (RuntimeException e) {
            // Évite de conserver un fichier orphelin si
            // l'enregistrement de ses métadonnées échoue.
            gridFSBucket.delete(fileId);
            throw e;
        }
    }

    public List<ReportDocument> getImagesByReportId(
            Integer reportId) {
        return reportRepository.findByReportId(reportId);
    }

    public GridFsResource getImage(String imageId) {
        ReportDocument document = reportRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Image introuvable : " + imageId));

        ObjectId fileId = new ObjectId(document.getStorageName());

        var file = gridFsTemplate.findOne(
                Query.query(Criteria.where("_id").is(fileId)));

        if (file == null) {
            throw new IllegalArgumentException(
                    "Fichier image introuvable dans GridFS.");
        }

        return gridFsTemplate.getResource(file);
    }

    public void deleteImage(String imageId) {
        ReportDocument document = reportRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Image introuvable : " + imageId));

        ObjectId fileId = new ObjectId(document.getStorageName());

        gridFSBucket.delete(fileId);
        reportRepository.delete(document);
    }
}
