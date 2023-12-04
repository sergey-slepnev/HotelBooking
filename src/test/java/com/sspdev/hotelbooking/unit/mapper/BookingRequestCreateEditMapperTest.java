package com.sspdev.hotelbooking.unit.mapper;

import com.sspdev.hotelbooking.database.entity.BookingRequest;
import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.PersonalInfo;
import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.database.repository.HotelRepository;
import com.sspdev.hotelbooking.database.repository.RoomRepository;
import com.sspdev.hotelbooking.database.repository.UserRepository;
import com.sspdev.hotelbooking.dto.BookingRequestCreateEditDto;
import com.sspdev.hotelbooking.mapper.BookingRequestCreateEditMapper;
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
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class BookingRequestCreateEditMapperTest {

    private static final Long EXISTENT_REQUEST_ID = 1L;
    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer FIRST_OWNER_ID = 4;
    private static final Integer EXISTENT_ROOM_ID = 1;
    private static final Integer EXISTENT_USER_ID = 1;

    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingRequestCreateEditMapper bookingRequestCreateEditMapper;

    @Test
    void map_shouldMapFromBookingRequestCreateEditDto_toBookingRequestEntity() {
        doReturn(Optional.of(getHotel())).when(hotelRepository).findById(EXISTENT_HOTEL_ID);
        doReturn(Optional.of(getRoom())).when(roomRepository).findById(EXISTENT_ROOM_ID);
        doReturn(Optional.of(getUser())).when(userRepository).findById(EXISTENT_USER_ID);

        var createDto = getBookingRequestCreateDto();
        var actuarBookingRequest = bookingRequestCreateEditMapper.map(createDto);
        var expectedBookingRequest = getBookingRequestEntity();

        assertThat(actuarBookingRequest).isEqualTo(expectedBookingRequest);
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
                .personalInfo(new PersonalInfo(
                        "TestFirstName",
                        "TestLastName",
                        LocalDate.of(2000, 10, 10)))
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

    private BookingRequestCreateEditDto getBookingRequestCreateDto() {
        return BookingRequestCreateEditDto.builder()
                .id(EXISTENT_REQUEST_ID)
                .createdAt(LocalDateTime.of(2023, 12, 1, 15, 15))
                .hotelId(EXISTENT_HOTEL_ID)
                .roomId(EXISTENT_ROOM_ID)
                .userId(EXISTENT_USER_ID)
                .checkIn(LocalDate.of(2023, 12, 1))
                .checkOut(LocalDate.of(2023, 12, 10))
                .status(Status.NEW)
                .build();
    }
}