package com.sspdev.hotelbooking.dto;

import com.sspdev.hotelbooking.database.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class HotelReadDto {

    Integer id;
    Integer ownerId;
    String name;
    Boolean available;
    Status status;
}