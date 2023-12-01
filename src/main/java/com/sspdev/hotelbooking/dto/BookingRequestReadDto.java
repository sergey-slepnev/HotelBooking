package com.sspdev.hotelbooking.dto;

import com.sspdev.hotelbooking.database.entity.enums.Status;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record BookingRequestReadDto(Long id,
                                    LocalDateTime createdAt,
                                    Integer hotelId,
                                    Integer roomId,
                                    Integer userId,
                                    LocalDate checkIn,
                                    LocalDate checkOut,
                                    Status status) {
}