package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.BookingRequest;
import com.sspdev.hotelbooking.dto.BookingRequestReadDto;
import org.springframework.stereotype.Component;

@Component
public class BookingRequestReadMapper implements Mapper<BookingRequest, BookingRequestReadDto> {

    @Override
    public BookingRequestReadDto map(BookingRequest request) {
        return new BookingRequestReadDto(
                request.getId(),
                request.getCreatedAt(),
                request.getHotel().getId(),
                request.getRoom().getId(),
                request.getUser().getId(),
                request.getCheckIn(),
                request.getCheckOut(),
                request.getStatus()
        );
    }
}