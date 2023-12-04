package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.BookingRequest;
import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.database.repository.HotelRepository;
import com.sspdev.hotelbooking.database.repository.RoomRepository;
import com.sspdev.hotelbooking.database.repository.UserRepository;
import com.sspdev.hotelbooking.dto.BookingRequestCreateEditDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookingRequestCreateEditMapper implements Mapper<BookingRequestCreateEditDto, BookingRequest> {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    public BookingRequest map(BookingRequestCreateEditDto createDto) {
        return BookingRequest.builder()
                .id(createDto.id())
                .createdAt(createDto.createdAt())
                .hotel(getHotel(createDto.hotelId()))
                .room(getRoom(createDto.roomId()))
                .user(getUser(createDto.userId()))
                .checkIn(createDto.checkIn())
                .checkOut(createDto.checkOut())
                .status(Status.NEW)
                .build();
    }

    private Hotel getHotel(Integer hotelId) {
        return Optional.ofNullable(hotelId)
                .flatMap(hotelRepository::findById)
                .orElse(null);
    }

    private Room getRoom(Integer roomId) {
        return Optional.ofNullable(roomId)
                .flatMap(roomRepository::findById)
                .orElse(null);
    }

    private User getUser(Integer userId) {
        return Optional.ofNullable(userId)
                .flatMap(userRepository::findById)
                .orElse(null);

    }
}