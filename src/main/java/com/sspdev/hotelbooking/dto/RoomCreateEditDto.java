package com.sspdev.hotelbooking.dto;

import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

public record RoomCreateEditDto(

        Integer hotelId,

        @NotNull(message = "{error.null.room_number}")
        @Positive(message = "{error.negative.room_number}")
        Integer roomNo,

        RoomType type,

        @NotNull(message = "{error.null.room_square}")
        @Positive(message = "{error.negative.room_square}")
        Double square,

        @NotNull(message = "{error.null.adult_beds}")
        @Positive(message = "{error.negative.adult_beds}")
        Integer adultBedCount,

        @NotNull(message = "{error.null.children_beds}")
        @PositiveOrZero(message = "{error.negative.children_beds}")
        Integer childrenBedCount,

        @NotNull(message = "{error.null.room_cost}")
        @Positive(message = "{error.negative.room_cost}")
        BigDecimal cost,

        @NotNull(message = "{error.null.room_floor}")
        @PositiveOrZero(message = "{error.negative.room_floor}")
        Integer floor,

        Boolean available,

        String description,

        List<RoomContentReadDto> contents
) {

}