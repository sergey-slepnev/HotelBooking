package com.sspdev.hotelbooking.service;

import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class ApplicationContentService {

    @Value("${spring.servlet.multipart.location}")
    private final String bucket;

    @Value("${my-app.content.no-image-available}")
    private final String contentNotFoundImage;

    @SneakyThrows
    public void uploadImage(MultipartFile content) {
        var inputStream = content.getInputStream();
        var fullImagePath = Path.of(bucket, content.getOriginalFilename());
        if (!Objects.equals(content.getOriginalFilename(), "")) {
            try (inputStream) {
                Files.createDirectories(fullImagePath.getParent());
                Files.write(fullImagePath, inputStream.readAllBytes(), CREATE, TRUNCATE_EXISTING);
            }
        }
    }

    @SneakyThrows
    public Optional<byte[]> getImage(String imagePath) {
        var fullImagePath = Path.of(bucket, imagePath);
        var contentNotFoundImagePath = Path.of(bucket, contentNotFoundImage);
        return Files.exists(fullImagePath)
                ? Optional.of(Files.readAllBytes(fullImagePath))
                : Optional.of(Files.readAllBytes(contentNotFoundImagePath));
    }

    @SneakyThrows
    public ContentType getContentType(MultipartFile content) {
        return Files.probeContentType(Path.of(requireNonNull(content.getOriginalFilename()))).startsWith("image")
                ? ContentType.PHOTO
                : ContentType.VIDEO;
    }

    @SneakyThrows
    public boolean isContentValid(MultipartFile content) {
        var contentPath = Path.of(requireNonNull(content.getOriginalFilename()));
        var contentType = Files.probeContentType(contentPath);
        return contentType != null && (contentType.startsWith("image") || contentType.startsWith("video"));
    }
}