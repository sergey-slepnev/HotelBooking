package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.dto.HotelReadDto;
import org.springframework.stereotype.Component;

@Component
public class HotelReadMapper implements Mapper<Hotel, HotelReadDto> {

    @Override
    public HotelReadDto map(Hotel object) {
        return new HotelReadDto(
                object.getId(),
                object.getOwner().getId(),
                object.getName(),
                object.isAvailable(),
                object.getStatus()
        );
    }
}