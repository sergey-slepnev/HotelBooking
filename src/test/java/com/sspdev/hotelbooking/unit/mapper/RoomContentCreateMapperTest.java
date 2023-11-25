package com.sspdev.hotelbooking.unit.mapper;

import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.database.entity.RoomContent;
import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.dto.RoomContentCreateDto;
import com.sspdev.hotelbooking.mapper.RoomContentCreateMapper;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
class RoomContentCreateMapperTest extends UnitTestBase {

    private static final Integer EXISTENT_ROOM_ID = 1;

    private final RoomContentCreateMapper roomContentCreateMapper;

    @Test
    void map_shouldMapFromRoomContentCreateDtoToRoomContent() {
        var roomContentCreateDto = getRoomContentCreateDto();
        var expectedContent = getRoomContent();

        var actualContent = roomContentCreateMapper.map(roomContentCreateDto);

        assertEquals(expectedContent, actualContent);
    }

    private RoomContentCreateDto getRoomContentCreateDto() {
        return new RoomContentCreateDto(
                new MockMultipartFile("RoomPhoto.jpg", "RoomPhoto.jpg", "application/octet-stream", "RoomPhoto.jpg".getBytes()),
                ContentType.PHOTO,
                EXISTENT_ROOM_ID
        );
    }

    private RoomContent getRoomContent() {
        return new RoomContent(
                1,
                getRoom(),
                "RoomPhoto.jpg",
                ContentType.PHOTO
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