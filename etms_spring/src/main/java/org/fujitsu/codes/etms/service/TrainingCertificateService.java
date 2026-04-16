package org.fujitsu.codes.etms.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.fujitsu.codes.etms.exception.InvalidInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TrainingCertificateService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".pdf", ".jpg", ".jpeg", ".png", ".webp");
    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024L * 1024L;
    private static final DateTimeFormatter FILE_STAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final Path certificateDirectory;

    public TrainingCertificateService(
            @Value("${etms.storage.training-certificates-dir:storage/training-certificates}") String certificateDirectory) {
        this.certificateDirectory = Paths.get(certificateDirectory).toAbsolutePath().normalize();
    }

    public String storeCertificate(Long trngInfoId, String trngCode, MultipartFile file, String currentCertificatePath) {
        validateFile(file);

        try {
            Files.createDirectories(certificateDirectory);
            deleteCertificateIfPresent(currentCertificatePath);

            String extension = extractExtension(file.getOriginalFilename());
            String safeTrngCode = trngCode == null || trngCode.isBlank()
                    ? "training"
                    : trngCode.replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = "training-" + trngInfoId + "-" + safeTrngCode + "-"
                    + FILE_STAMP.format(LocalDateTime.now()) + extension;
            Path target = certificateDirectory.resolve(fileName).normalize();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return fileName;
        } catch (IOException ex) {
            throw new InvalidInputException("Failed to store training certificate");
        }
    }

    public Resource loadCertificate(String certificatePath) {
        if (certificatePath == null || certificatePath.isBlank()) {
            throw new InvalidInputException("Training certificate is not available");
        }

        try {
            Path file = certificateDirectory.resolve(Paths.get(certificatePath).getFileName().toString()).normalize();
            if (!Files.exists(file) || !file.startsWith(certificateDirectory)) {
                throw new InvalidInputException("Training certificate is not available");
            }

            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new InvalidInputException("Training certificate is not available");
            }
            return resource;
        } catch (IOException ex) {
            throw new InvalidInputException("Training certificate is not available");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidInputException("Training certificate file is required");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new InvalidInputException("Training certificate must be 10 MB or smaller");
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new InvalidInputException("Training certificate must be PDF, JPG, PNG, or WEBP");
        }
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new InvalidInputException("Training certificate file name is invalid");
        }
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == originalFilename.length() - 1) {
            throw new InvalidInputException("Training certificate must have a valid file extension");
        }
        return originalFilename.substring(dotIndex).toLowerCase();
    }

    private void deleteCertificateIfPresent(String currentCertificatePath) {
        if (currentCertificatePath == null || currentCertificatePath.isBlank()) {
            return;
        }

        try {
            Path existing = certificateDirectory.resolve(Paths.get(currentCertificatePath).getFileName().toString()).normalize();
            if (existing.startsWith(certificateDirectory)) {
                Files.deleteIfExists(existing);
            }
        } catch (IOException ignored) {
            // Best effort cleanup only.
        }
    }
}
