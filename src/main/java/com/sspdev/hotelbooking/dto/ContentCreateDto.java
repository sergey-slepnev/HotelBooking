package com.sspdev.hotelbooking.dto;

import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@SuperBuilder
@Data
public abstract class ContentCreateDto {

    private MultipartFile content;

    private ContentType contentType;
}