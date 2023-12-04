package com.sspdev.hotelbooking.unit.service;

import com.sspdev.hotelbooking.database.entity.BookingRequest;
import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.database.repository.BookingRequestRepository;
import com.sspdev.hotelbooking.dto.BookingRequestCreateEditDto;
import com.sspdev.hotelbooking.dto.BookingRequestReadDto;
import com.sspdev.hotelbooking.mapper.BookingRequestCreateEditMapper;
import com.sspdev.hotelbooking.mapper.BookingRequestReadMapper;
import com.sspdev.hotelbooking.service.BookingRequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingRequestServiceTest {

    private static final Long EXISTENT_REQUEST_ID = 1L;
    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer FIRST_OWNER_ID = 4;
    private static final Integer EXISTENT_ROOM_ID = 1;

    @Mock
    private BookingRequestRepository bookingRequestRepository;
    @Mock
    private BookingRequestReadMapper bookingRequestReadMapper;
    @Mock
    private BookingRequestCreateEditMapper bookingRequestCreateEditMapper;
    @InjectMocks
    private BookingRequestService bookingRequestService;

    @Test
    void findById_shouldFindRequestById_ifRequestExists() {
        var existentRequest = getBookingRequestEntity();
        when(bookingRequestRepository.findById(EXISTENT_REQUEST_ID)).thenReturn(Optional.of(existentRequest));
        var requestReadDto = getRequestReadDto();
        when(bookingRequestReadMapper.map(existentRequest)).thenReturn(requestReadDto);

        var expectedRequest = bookingRequestService.findById(EXISTENT_REQUEST_ID);

        expectedRequest.ifPresent(request -> {
            assertAll(() -> {
                        assertThat(request.id()).isEqualTo(existentRequest.getId());
                        assertThat(request.createdAt()).isEqualTo(existentRequest.getCreatedAt());
                        assertThat(request.hotelName()).isEqualTo(existentRequest.getHotel().getName());
                        assertThat(request.roomNo()).isEqualTo(existentRequest.getRoom().getRoomNo());
                        assertThat(request.userName()).isEqualTo(existentRequest.getUser().getUsername());
                        assertThat(request.checkIn()).isEqualTo(existentRequest.getCheckIn());
                        assertThat(request.checkOut()).isEqualTo(existentRequest.getCheckOut());
                        assertThat(request.status()).isEqualTo(existentRequest.getStatus());
                    }
            );
        });
    }

    @Test
    void create_shouldCreateNewBookingRequest() {
        var createDto = getBookingRequestCreateDto();
        var bookingRequestEntity = getBookingRequestEntity();
        var requestReadDto = getRequestReadDto();
        when(bookingRequestCreateEditMapper.map(createDto)).thenReturn(bookingRequestEntity);
        when(bookingRequestRepository.save(bookingRequestEntity)).thenReturn(bookingRequestEntity);
        when(bookingRequestReadMapper.map(bookingRequestEntity)).thenReturn(requestReadDto);

        var actualReadDto = bookingRequestService.create(createDto);

        assertThat(actualReadDto).isEqualTo(requestReadDto);
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
                .username("TestUser")
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

    private BookingRequestReadDto getRequestReadDto() {
        return BookingRequestReadDto.builder()
                .id(EXISTENT_REQUEST_ID)
                .createdAt(LocalDateTime.of(2023, 12, 1, 15, 15))
                .hotelName("MoscowPlaza")
                .roomNo(1)
                .userName("TestUser")
                .checkIn(LocalDate.of(2023, 12, 1))
                .checkOut(LocalDate.of(2023, 12, 10))
                .status(Status.NEW)
                .build();
    }

    private BookingRequestCreateEditDto getBookingRequestCreateDto() {
        return BookingRequestCreateEditDto.builder()
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