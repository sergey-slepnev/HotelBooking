package com.sspdev.hotelbooking.unit.mapper;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.RoomCreateEditDto;
import com.sspdev.hotelbooking.mapper.RoomCreateEditMapper;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
public class RoomCreateEditMapperTest extends UnitTestBase {

    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer EXISTENT_ROOM_ID = 1;

    private final RoomCreateEditMapper roomCreateEditMapper;

    @Test
    void map_shouldMapFromCreateEditDtoToRoom() {
        var expectedRoom = getRoom();
        var roomCreateEditDto = getRoomCreateEditDto();

        var actualRoom = roomCreateEditMapper.map(roomCreateEditDto);

        assertEquals(expectedRoom, actualRoom);
    }

    @Test
    void map_shouldCopyFromRoomCreateEditDtoToExistentRoom() {
        var room = getRoom();
        var roomCreateEditDtoToUpdate = getRoomCreateEditDtoToUpdate();
        var expectedRoom = getUpdatedRoom();

        var actualRoom = roomCreateEditMapper.map(roomCreateEditDtoToUpdate, room);

        assertEquals(expectedRoom, actualRoom);
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
                .description("Отличный номер")
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

    private RoomCreateEditDto getRoomCreateEditDto() {
        return new RoomCreateEditDto(
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

    private RoomCreateEditDto getRoomCreateEditDtoToUpdate() {
        return new RoomCreateEditDto(
                EXISTENT_HOTEL_ID,
                5,
                RoomType.QDPL,
                55.5,
                3,
                2,
                BigDecimal.valueOf(2700),
                2,
                true,
                "Отличный номер",
                null
        );
    }

    private Room getUpdatedRoom() {
        return new Room(
                EXISTENT_ROOM_ID,
                getHotel(),
                5,
                RoomType.QDPL,
                55.5,
                3,
                2,
                BigDecimal.valueOf(2700),
                true,
                2,
                "Отличный номер",
                null,
                null);
    }
}