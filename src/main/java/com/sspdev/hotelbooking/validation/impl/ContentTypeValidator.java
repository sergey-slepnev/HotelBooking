package com.sspdev.hotelbooking.validation.impl;

import com.sspdev.hotelbooking.service.ApplicationContentService;
import com.sspdev.hotelbooking.validation.CheckContentType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
public class ContentTypeValidator implements ConstraintValidator<CheckContentType, MultipartFile> {

    private final ApplicationContentService applicationContentService;

    @Override
    public boolean isValid(MultipartFile content, ConstraintValidatorContext context) {
        return applicationContentService.isContentValid(content);
    }
}