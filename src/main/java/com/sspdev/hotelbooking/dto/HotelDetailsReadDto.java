package com.sspdev.hotelbooking.dto;

import com.sspdev.hotelbooking.database.entity.enums.Star;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class HotelDetailsReadDto {

    Integer id;
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