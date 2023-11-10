package com.sspdev.hotelbooking.service;

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

@Service
@RequiredArgsConstructor
public class ApplicationContentService {

    @Value("${spring.servlet.multipart.location}")
    private final String bucket;

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
        return Files.exists(fullImagePath)
                ? Optional.of(Files.readAllBytes(fullImagePath))
                : Optional.empty();
    }
}