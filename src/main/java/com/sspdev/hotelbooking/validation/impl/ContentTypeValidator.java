package com.sspdev.hotelbooking.validation.impl;

import com.sspdev.hotelbooking.service.ApplicationContentService;
import com.sspdev.hotelbooking.validation.CheckContentType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

//@RequiredArgsConstructor
public class ContentTypeValidator implements ConstraintValidator<CheckContentType, MultipartFile> {

    private ApplicationContentService applicationContentService;

    public ContentTypeValidator(ApplicationContentService applicationContentService) {
        this.applicationContentService = applicationContentService;
    }

    public ContentTypeValidator() {

    }

    @Override
    public boolean isValid(MultipartFile content, ConstraintValidatorContext context) {
        return applicationContentService.isContentValid(content);
    }
}