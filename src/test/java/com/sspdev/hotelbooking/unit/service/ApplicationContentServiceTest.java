package com.sspdev.hotelbooking.unit.service;

import com.sspdev.hotelbooking.service.ApplicationContentService;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;

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
}