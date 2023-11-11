package com.sspdev.hotelbooking.dto;

import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.validation.CheckContentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@SuperBuilder
@Data
@FieldNameConstants
public abstract class ContentCreateDto {

    @CheckContentType
    private MultipartFile content;

    private ContentType contentType;
}