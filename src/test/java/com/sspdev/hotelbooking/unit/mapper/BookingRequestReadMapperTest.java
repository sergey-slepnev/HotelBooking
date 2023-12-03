package com.sspdev.hotelbooking.unit.mapper;

import com.sspdev.hotelbooking.database.entity.BookingRequest;
import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.BookingRequestReadDto;
import com.sspdev.hotelbooking.mapper.BookingRequestReadMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingRequestReadMapperTest {

    private static final Long EXISTENT_REQUEST_ID = 1L;
    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer FIRST_OWNER_ID = 4;
    private static final Integer EXISTENT_ROOM_ID = 1;

    private BookingRequestReadMapper bookingRequestReadMapper;

    @BeforeEach
    public void init() {
        bookingRequestReadMapper = new BookingRequestReadMapper();
    }

    @Test
    void map_shouldMapFromBookingRequestToBookingRequestReadDto() {
        var bookingRequestEntity = getBookingRequestEntity();
        var expectedReadDto = getBookingRequestReadDto();

        var actualDto = bookingRequestReadMapper.map(bookingRequestEntity);

        assertThat(actualDto).isEqualTo(expectedReadDto);
    }

    private BookingRequest getBookingRequestEntity() {
        return BookingRequest.builder()
                .id(EXISTENT_REQUEST_ID)
                .createdAt(LocalDateTime.of(2023, 12, 1, 15, 15))
                .hotel(getHotel())
                .room(getRoom())
                .user(getUser())
                .checkIn(LocalDate.of(2023, 12, 1))
                .checkOut(LocalDate.of(2023, 12, 10))
                .status(Status.NEW)
                .build();
    }

    private Hotel getHotel() {
        return Hotel.builder()
                .id(EXISTENT_HOTEL_ID)
                .owner(getUser())
                .name("MoscowPlaza")
                .available(true)
                .status(Status.APPROVED)
                .build();
    }

    private User getUser() {
        return User.builder()
                .id(FIRST_OWNER_ID)
                .role(Role.ADMIN)
                .build();
    }

    private Room getRoom() {
        return Room.builder()
                .id(EXISTENT_ROOM_ID)
                .hotel(getHotel())
                .roomNo(1)
                .type(RoomType.DBL)
                .square(44.4)
                .adultBedCount(3)
                .childrenBedCount(0)
                .cost(BigDecimal.valueOf(1900))
                .floor(1)
                .available(true)
                .description("Отличный отель")
                .roomContents(null)
                .build();
    }

    private BookingRequestReadDto getBookingRequestReadDto() {
        return BookingRequestReadDto.builder()
                .id(EXISTENT_REQUEST_ID)
                .createdAt(LocalDateTime.of(2023, 12, 1, 15, 15))
                .hotelId(EXISTENT_HOTEL_ID)
                .roomId(EXISTENT_ROOM_ID)
                .userId(FIRST_OWNER_ID)
                .checkIn(LocalDate.of(2023, 12, 1))
                .checkOut(LocalDate.of(2023, 12, 10))
                .status(Status.NEW)
                .build();
    }
}