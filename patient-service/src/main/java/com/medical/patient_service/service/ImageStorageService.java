package com.medical.patient_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final Path storageDirectory;

    public ImageStorageService(
            @Value("${app.storage.directory}") String storageDirectory) {

        this.storageDirectory = Paths.get(storageDirectory)
                .toAbsolutePath()
                .normalize();

        System.out.println("DOSSIER STORAGE = " + this.storageDirectory);

        try {
            Files.createDirectories(this.storageDirectory);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Impossible de créer le dossier de stockage : "
                            + this.storageDirectory,
                    e
            );
        }
    }

    public String store(MultipartFile file, Integer reportId) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide.");
        }

        try {

            Path reportDirectory = storageDirectory
                    .resolve(String.valueOf(reportId))
                    .normalize();

            Files.createDirectories(reportDirectory);

            String originalFileName = file.getOriginalFilename();

            String extension = "";

            if (originalFileName != null
                    && originalFileName.contains(".")) {

                extension = originalFileName.substring(
                        originalFileName.lastIndexOf(".")
                );
            }

            String storageName = UUID.randomUUID() + extension;

            Path destination = reportDirectory
                    .resolve(storageName)
                    .normalize();

            if (!destination.startsWith(reportDirectory)) {
                throw new RuntimeException("Nom de fichier invalide.");
            }

            Files.copy(
                    file.getInputStream(),
                    destination
            );

            return storageName;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Impossible d'enregistrer le fichier.",
                    e
            );
        }
    }

    public String getStoragePath(
            Integer reportId,
            String storageName) {

        return storageDirectory
                .resolve(String.valueOf(reportId))
                .resolve(storageName)
                .normalize()
                .toString();
    }

    public Resource load(
            Integer reportId,
            String storageName) {

        if (storageName == null || storageName.isBlank()) {
            throw new IllegalArgumentException(
                    "Nom de fichier invalide."
            );
        }

        Path reportDirectory = storageDirectory
                .resolve(String.valueOf(reportId))
                .normalize();

        Path file = reportDirectory
                .resolve(storageName)
                .normalize();

        if (!file.startsWith(reportDirectory)) {
            throw new IllegalArgumentException(
                    "Accès au fichier interdit."
            );
        }

        if (!Files.exists(file)
                || !Files.isRegularFile(file)) {

            throw new RuntimeException(
                    "Image introuvable : " + storageName
            );
        }

        return new FileSystemResource(file);
    }

    public String getContentType(
            Integer reportId,
            String storageName) {

        if (storageName == null || storageName.isBlank()) {
            return "application/octet-stream";
        }

        Path reportDirectory = storageDirectory
                .resolve(String.valueOf(reportId))
                .normalize();

        Path file = reportDirectory
                .resolve(storageName)
                .normalize();

        if (!file.startsWith(reportDirectory)) {
            throw new IllegalArgumentException(
                    "Accès au fichier interdit."
            );
        }

        try {

            String contentType =
                    Files.probeContentType(file);

            if (contentType != null) {
                return contentType;
            }

        } catch (IOException e) {

            System.out.println(
                    "Impossible de déterminer le type MIME : "
                            + file
            );
        }

        return "application/octet-stream";
    }
}