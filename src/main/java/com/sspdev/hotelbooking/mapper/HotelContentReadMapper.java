package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.HotelContent;
import com.sspdev.hotelbooking.dto.HotelContentReadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotelContentReadMapper implements Mapper<HotelContent, HotelContentReadDto> {

    @Override
    public HotelContentReadDto map(HotelContent content) {
        return new HotelContentReadDto(
                content.getId(),
                content.getLink(),
                content.getType().name(),
                content.getHotel().getId());
    }
}