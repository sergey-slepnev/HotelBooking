package com.sspdev.hotelbooking.dto.filter;

import com.sspdev.hotelbooking.database.entity.enums.Status;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BookingRequestFilter(LocalDateTime createdAtFrom,
                                   LocalDateTime createdAtTo,
                                   String hotelName,
                                   String username,
                                   Status status) {

}
