package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.BookingRequest;
import com.sspdev.hotelbooking.dto.BookingRequestReadDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class BookingRequestReadMapper implements Mapper<BookingRequest, BookingRequestReadDto> {

    @Override
    public BookingRequestReadDto map(BookingRequest request) {
        return new BookingRequestReadDto(
                request.getId(),
                request.getCreatedAt(),
                request.getHotel().getName(),
                request.getRoom().getRoomNo(),
                request.getUser().getUsername(),
                request.getUser().getPersonalInfo().getFirstname(),
                request.getUser().getPersonalInfo().getLastname(),
                request.getCheckIn(),
                request.getCheckOut(),
                request.getStatus(),
                getDaysToStay(request.getCheckIn(), request.getCheckOut()),
                request.getRoom().getCost(),
                getTotalCost(request.getRoom().getCost(), request.getCheckIn(), request.getCheckOut())
        );
    }

    private BigDecimal getTotalCost(BigDecimal costPerDay, LocalDate checkIn, LocalDate checkOut) {
        var daysToStay = getDaysToStay(checkIn, checkOut);
        return costPerDay.multiply(BigDecimal.valueOf(daysToStay));
    }

    public long getDaysToStay(LocalDate checkIn, LocalDate checkOut) {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }
}