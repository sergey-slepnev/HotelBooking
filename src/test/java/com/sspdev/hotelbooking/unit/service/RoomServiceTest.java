package com.sspdev.hotelbooking.unit.service;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.database.repository.RoomRepository;
import com.sspdev.hotelbooking.dto.RoomReadDto;
import com.sspdev.hotelbooking.mapper.RoomReadMapper;
import com.sspdev.hotelbooking.service.RoomService;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
public class RoomServiceTest extends UnitTestBase {

    private static final Integer EXISTENT_ROOM_ID = 1;
    private static final Integer EXISTENT_HOTEL_ID = 1;

    @MockBean
    private final RoomReadMapper roomReadMapper;

    @MockBean
    private final RoomRepository roomRepository;

    @InjectMocks
    private final RoomService roomService;

    @Test
    void findById_shouldFindHotelById_whenHotelsExist() {
        var room = getRoom();
        when(roomRepository.findById(EXISTENT_ROOM_ID)).thenReturn(Optional.of(room));
        var roomReadDto = getRoomReadDto();
        when(roomReadMapper.map(room)).thenReturn(roomReadDto);

        var actualRoom = roomService.findById(EXISTENT_ROOM_ID);
        assertThat(actualRoom).isPresent();
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

    private Hotel getHotel() {
        return Hotel.builder()
                .id(EXISTENT_HOTEL_ID)
                .owner(null)
                .name("MoscowPlaza")
                .available(true)
                .status(Status.APPROVED)
                .build();
    }

    private RoomReadDto getRoomReadDto() {
        return new RoomReadDto(
                EXISTENT_ROOM_ID,
                EXISTENT_HOTEL_ID,
                1,
                RoomType.DBL,
                44.4,
                3,
                0,
                BigDecimal.valueOf(1900),
                1,
                true,
                "Отличный отель",
                null
        );
    }
}