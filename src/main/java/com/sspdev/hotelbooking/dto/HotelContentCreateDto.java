package com.sspdev.hotelbooking.dto;

import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class HotelContentCreateDto extends ContentCreateDto {

    Integer hotelId;

    public HotelContentCreateDto(MultipartFile content, ContentType contentType, Integer hotelId) {
        super(content, contentType);
        this.hotelId = hotelId;
    }
}