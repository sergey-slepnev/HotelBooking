package com.sspdev.hotelbooking.dto;

import com.sspdev.hotelbooking.database.entity.enums.Star;
import lombok.Value;

@Value
public class HotelDetailsCreateEditDto {

    Integer hotelId;

    String phoneNumber;

    String country;

    String locality;

    String area;

    String street;

    Integer floorCount;

    Star star;

    String description;
}