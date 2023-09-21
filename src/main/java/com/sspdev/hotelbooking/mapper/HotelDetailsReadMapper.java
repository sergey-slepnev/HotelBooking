package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.HotelDetails;
import com.sspdev.hotelbooking.dto.HotelDetailsReadDto;
import org.springframework.stereotype.Component;

@Component
public class HotelDetailsReadMapper implements Mapper<HotelDetails, HotelDetailsReadDto> {

    @Override
    public HotelDetailsReadDto map(HotelDetails object) {
        return new HotelDetailsReadDto(
                object.getId(),
                object.getHotel().getId(),
                object.getPhoneNumber(),
                object.getCountry(),
                object.getLocality(),
                object.getArea(),
                object.getStreet(),
                object.getFloorCount(),
                object.getStar(),
                object.getDescription()
        );
    }
}