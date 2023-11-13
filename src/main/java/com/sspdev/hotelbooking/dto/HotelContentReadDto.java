package com.sspdev.hotelbooking.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class HotelContentReadDto extends ContentReadDto {

    private Integer hotelId;

    public HotelContentReadDto(Integer id, String link, String type, Integer hotelId) {
        super(id, link, type);
        this.hotelId = hotelId;
    }
}