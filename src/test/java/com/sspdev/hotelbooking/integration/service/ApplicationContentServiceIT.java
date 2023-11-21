package com.sspdev.hotelbooking.integration.service;

import com.sspdev.hotelbooking.service.ApplicationContentService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.shaded.com.google.common.net.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
@SpringBootTest
class ApplicationContentServiceIT {

    @Value("${spring.servlet.multipart.location}")
    private final String bucket;
    @Value("${my-app.content.no-image-available}")
    private final String contentNotFoundImage;
    private final ApplicationContentService applicationContentService;
    private MockMultipartFile dummyImage;
    private Path fullPathToImage;

    @BeforeEach
    void initFileAndPath() {
        var image = "ImageToTestDeleteContentMethod.jpg";
        dummyImage = new MockMultipartFile(image, image, MediaType.ANY_IMAGE_TYPE.type(), image.getBytes());
        fullPathToImage = Path.of(bucket, dummyImage.getName());
    }

    @Test
    void uploadImage_shouldUploadImage_whenImageIsValid() {
        var isFileExist = Files.exists(fullPathToImage);
        assertThat(isFileExist).isFalse();

        applicationContentService.uploadImage(dummyImage);
        var isFileUploaded = Files.exists(fullPathToImage);

        assertThat(isFileUploaded).isTrue();
    }

    @Test
    void deleteContentFromStore_shouldDeleteContentFromStore_whenContentExists() throws IOException {
        Files.write(fullPathToImage, dummyImage.getBytes(), CREATE, TRUNCATE_EXISTING);
        var isExistBeforeDeleting = Files.exists(fullPathToImage);
        assertThat(isExistBeforeDeleting).isTrue();

        applicationContentService.deleteContentFromStore(dummyImage.getName());

        var isExistAfterDeleting = Files.exists(fullPathToImage);
        assertThat(isExistAfterDeleting).isFalse();
    }

    @Test
    void deleteContentFromStore_shouldNotDeleteContentFromStore_whenNameIsNoImageAvailable() {
        var pathToImage = Path.of(bucket, contentNotFoundImage);

        var isNoImageAvailableExist = Files.exists(pathToImage);
        assertThat(isNoImageAvailableExist).isTrue();

        applicationContentService.deleteContentFromStore(contentNotFoundImage);

        var isExistAfterDeleting = Files.exists(pathToImage);
        assertThat(isExistAfterDeleting).isTrue();
    }
}