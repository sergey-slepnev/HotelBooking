package com.sspdev.hotelbooking.unit.service;

import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.service.ApplicationContentService;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
class ApplicationContentServiceTest extends UnitTestBase {

    @MockBean
    private final InputStream inputStream;

    @MockBean
    private final MockMultipartFile mockMultipartFile;

    @InjectMocks
    private final ApplicationContentService applicationContentService;

    @Test
    void uploadImage_shouldUploadImage_whenImageExists() throws IOException {
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test_photo.jpg");
        when(inputStream.readAllBytes()).thenReturn("test_photo.jpg".getBytes());
        when(mockMultipartFile.getInputStream()).thenReturn(inputStream);

        applicationContentService.uploadImage(mockMultipartFile);
    }

    @Test
    void getImage_shouldReturnImage_whenImageExists() throws IOException {
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test_photo.jpg");

        var image = applicationContentService.getImage(mockMultipartFile.getOriginalFilename());

        assertThat(image).isPresent();
    }

    @Test
    void getContentType_shouldReturnPhotoType() throws IOException {
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test_photo.jpg");

        var actualType = applicationContentService.getContentType(mockMultipartFile);

        assertEquals(ContentType.PHOTO, actualType);
    }

    @ParameterizedTest
    @ValueSource(strings = {"test.jpg", "test.png", "test.mp4", "test.mkv"})
    void isContentValid_shouldReturnTrue_whenContentValid(String fileName) {
        when(mockMultipartFile.getOriginalFilename()).thenReturn(fileName);

        var actualResult = applicationContentService.isContentValid(mockMultipartFile);

        assertTrue(actualResult);
    }

    @ParameterizedTest
    @ValueSource(strings = {"test.exe", "test.pdf", "test.zip", "test.doc"})
    void isContentValid_shouldReturnFalse_whenContentInvalid(String fileName) {
        when(mockMultipartFile.getOriginalFilename()).thenReturn(fileName);

        var actualResult = applicationContentService.isContentValid(mockMultipartFile);

        assertFalse(actualResult);
    }
}