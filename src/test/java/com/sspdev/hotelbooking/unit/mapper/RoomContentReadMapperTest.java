package com.sspdev.hotelbooking.unit.mapper;

import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.database.entity.RoomContent;
import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.dto.RoomContentReadDto;
import com.sspdev.hotelbooking.mapper.RoomContentReadMapper;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
class RoomContentReadMapperTest extends UnitTestBase {

    private static final Integer EXISTENT_ROOM_ID = 1;
    private static final Integer EXISTENT_ROOM_CONTENT_ID = 1;

    private final RoomContentReadMapper roomContentReadMapper;

    @Test
    void map_shouldMapFromRoomContentToRoomContentReadDto() {
        var roomContent = getRoomContent();
        var expectedContent = getRoomContentReadDto();

        var actualContent = roomContentReadMapper.map(roomContent);

        assertEquals(expectedContent, actualContent);
    }

    private RoomContent getRoomContent() {
        return new RoomContent(
                1,
                getRoom(),
                "RoomPhoto.jpg",
                ContentType.PHOTO
        );
    }

    private RoomContentReadDto getRoomContentReadDto() {
        return new RoomContentReadDto(
                EXISTENT_ROOM_CONTENT_ID,
                "RoomPhoto.jpg",
                ContentType.PHOTO.name(),
                EXISTENT_ROOM_ID
        );
    }

    private Room getRoom() {
        return Room.builder()
                .id(EXISTENT_ROOM_ID)
                .hotel(null)
                .roomNo(1)
                .build();
    }
}