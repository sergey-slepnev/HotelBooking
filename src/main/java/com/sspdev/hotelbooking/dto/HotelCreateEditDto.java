package com.sspdev.hotelbooking.dto;

import com.sspdev.hotelbooking.database.entity.enums.Status;
import lombok.Value;

@Value
public class HotelCreateEditDto {

    Integer ownerId;

    String name;

    Boolean available;

    Status status;
}