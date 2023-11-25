package com.sspdev.hotelbooking.unit.mapper;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.RoomReadDto;
import com.sspdev.hotelbooking.mapper.RoomReadMapper;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
public class RoomReadMapperTest extends UnitTestBase {

    private static final Integer EXISTENT_ROOM_ID = 1;
    private static final Integer EXISTENT_HOTEL_ID = 1;

    private final RoomReadMapper roomReadMapper;

    @Test
    void map_shouldMapRoomReadDtoFromHotel() {
        var room = getRoom();
        var roomReadDto = getRoomReadDto();

        var actualResult = roomReadMapper.map(room);

        assertEquals(roomReadDto, actualResult);
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
                .roomContents(Collections.emptyList())
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
                Collections.emptyList()
        );
    }
}