package com.sspdev.hotelbooking.dto;

import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
public class RoomReadDto {

    Integer id;
    Integer hotelId;
    Integer roomNo;
    RoomType type;
    Double square;
    Integer adultBedCount;
    Integer childrenBedCount;
    BigDecimal cost;
    Integer floor;
    Boolean available;
    String description;
    List<RoomContentReadDto> content;

}