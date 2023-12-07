package com.sspdev.hotelbooking.dto.filter;

import com.sspdev.hotelbooking.database.entity.enums.Star;
import com.sspdev.hotelbooking.validation.CheckCostMaxNotLessCostMin;
import com.sspdev.hotelbooking.validation.CheckDateFromNotBeforeDateTo;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@FieldNameConstants
@CheckCostMaxNotLessCostMin
@CheckDateFromNotBeforeDateTo
public record RoomFilter(

        @NotBlank
        String country,

        @NotBlank
        String locality,

        Star star,

        BigDecimal costFrom,

        BigDecimal costTo,

        Integer adultBedCount,

        Integer childrenBedCount,

        @FutureOrPresent
        LocalDate checkIn,

        @Future
        LocalDate checkOut,

        Boolean available) {
}