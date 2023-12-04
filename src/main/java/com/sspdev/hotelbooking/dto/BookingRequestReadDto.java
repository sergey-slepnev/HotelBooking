package com.sspdev.hotelbooking.dto;

import com.sspdev.hotelbooking.database.entity.enums.Status;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record BookingRequestReadDto(Long id,
                                    LocalDateTime createdAt,
                                    String hotelName,
                                    Integer roomNo,
                                    String userName,
                                    String firstName,
                                    String lastName,
                                    LocalDate checkIn,
                                    LocalDate checkOut,
                                    Status status,
                                    Long daysToStay,
                                    BigDecimal costPerDay,
                                    BigDecimal totalCost) {
}