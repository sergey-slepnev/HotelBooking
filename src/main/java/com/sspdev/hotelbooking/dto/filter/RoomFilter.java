package com.sspdev.hotelbooking.dto.filter;

import com.sspdev.hotelbooking.database.entity.enums.Star;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@FieldNameConstants
public record RoomFilter(String country,
                         String locality,
                         Star star,
                         BigDecimal costFrom,
                         BigDecimal costTo,
                         Integer adultBedCount,
                         Integer childrenBedCount,
                         LocalDate checkIn,
                         LocalDate checkOut,
                         Boolean available) {

}