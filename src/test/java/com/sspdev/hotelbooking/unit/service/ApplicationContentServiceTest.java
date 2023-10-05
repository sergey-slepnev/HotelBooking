package com.sspdev.hotelbooking.unit.service;

import com.sspdev.hotelbooking.service.ApplicationContentService;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.when;

@RequiredArgsConstructor
class ApplicationContentServiceTest extends UnitTestBase {

    @MockBean
    private final InputStream inputStream;

    @InjectMocks
    private final ApplicationContentService applicationContentService;

    @Test
    void uploadImage_shouldUploadImage_whenImageExists() throws IOException {
        when(inputStream.readAllBytes()).thenReturn("someContent".getBytes());

        applicationContentService.uploadImage("fullImagePath", inputStream);
    }
}