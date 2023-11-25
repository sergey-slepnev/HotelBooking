package com.sspdev.hotelbooking.dto;

import com.sspdev.hotelbooking.database.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldNameConstants;

@Value
@FieldNameConstants
@Builder
@AllArgsConstructor
public class HotelCreateEditDto {

    Integer ownerId;

    @NotBlank(message = "{error.blank.hotel_name}")
    @Size(min = 1, max = 128, message = "{error.length.hotel_name}")
    String name;

    Boolean available;

    Status status;
}