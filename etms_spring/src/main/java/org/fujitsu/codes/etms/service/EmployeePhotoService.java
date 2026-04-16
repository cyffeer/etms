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
public class EmployeePhotoService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024L * 1024L;
    private static final DateTimeFormatter FILE_STAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final Path photoDirectory;

    public EmployeePhotoService(@Value("${etms.storage.employee-photos-dir:storage/employee-photos}") String photoDirectory) {
        this.photoDirectory = Paths.get(photoDirectory).toAbsolutePath().normalize();
    }

    public String storePhoto(Long employeeId, String employeeCode, MultipartFile file, String currentPhotoPath) {
        validateFile(file);

        try {
            Files.createDirectories(photoDirectory);
            deletePhotoIfPresent(currentPhotoPath);

            String extension = extractExtension(file.getOriginalFilename());
            String safeEmployeeCode = employeeCode == null || employeeCode.isBlank()
                    ? "employee"
                    : employeeCode.replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = "employee-" + employeeId + "-" + safeEmployeeCode + "-"
                    + FILE_STAMP.format(LocalDateTime.now()) + extension;
            Path target = photoDirectory.resolve(fileName).normalize();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return fileName;
        } catch (IOException ex) {
            throw new InvalidInputException("Failed to store employee photo");
        }
    }

    public Resource loadPhoto(String photoPath) {
        if (photoPath == null || photoPath.isBlank()) {
            throw new InvalidInputException("Employee photo is not available");
        }

        try {
            Path file = photoDirectory.resolve(Paths.get(photoPath).getFileName().toString()).normalize();
            if (!Files.exists(file) || !file.startsWith(photoDirectory)) {
                throw new InvalidInputException("Employee photo is not available");
            }

            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new InvalidInputException("Employee photo is not available");
            }
            return resource;
        } catch (IOException ex) {
            throw new InvalidInputException("Employee photo is not available");
        }
    }

    public String detectContentType(Resource resource) {
        try {
            String detected = Files.probeContentType(Path.of(resource.getFile().getAbsolutePath()));
            return detected == null ? "application/octet-stream" : detected;
        } catch (IOException ex) {
            return "application/octet-stream";
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidInputException("Employee photo file is required");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new InvalidInputException("Employee photo must be 5 MB or smaller");
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new InvalidInputException("Employee photo must be JPG, PNG, GIF, or WEBP");
        }
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new InvalidInputException("Employee photo file name is invalid");
        }
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == originalFilename.length() - 1) {
            throw new InvalidInputException("Employee photo must have a valid file extension");
        }
        return originalFilename.substring(dotIndex).toLowerCase();
    }

    private void deletePhotoIfPresent(String currentPhotoPath) {
        if (currentPhotoPath == null || currentPhotoPath.isBlank()) {
            return;
        }

        try {
            Path existing = photoDirectory.resolve(Paths.get(currentPhotoPath).getFileName().toString()).normalize();
            if (existing.startsWith(photoDirectory)) {
                Files.deleteIfExists(existing);
            }
        } catch (IOException ignored) {
            // Best effort cleanup only.
        }
    }
}
