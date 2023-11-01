package com.sspdev.hotelbooking.dto;

import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class RoomContentCreateDto extends ContentCreateDto {

    Integer roomId;

    public RoomContentCreateDto(MultipartFile content, ContentType contentType, Integer roomId) {
        super(content, contentType);
        this.roomId = roomId;
    }
}