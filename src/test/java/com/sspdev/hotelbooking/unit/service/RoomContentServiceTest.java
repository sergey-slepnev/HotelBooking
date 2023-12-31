package com.sspdev.hotelbooking.unit.service;

import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.database.entity.RoomContent;
import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import com.sspdev.hotelbooking.database.repository.RoomContentRepository;
import com.sspdev.hotelbooking.dto.RoomContentCreateDto;
import com.sspdev.hotelbooking.mapper.RoomContentCreateMapper;
import com.sspdev.hotelbooking.service.RoomContentService;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
public class RoomContentServiceTest extends UnitTestBase {

    private static final Integer EXISTENT_ROOM_ID = 1;
    private static final Integer EXISTENT_ROOM_CONTENT_ID = 1;
    private static final Integer NON_EXISTENT_ROOM_CONTENT_ID = 999;

    @MockBean
    private final RoomContentRepository roomContentRepository;

    @MockBean
    private final RoomContentCreateMapper roomContentCreateMapper;

    @InjectMocks
    private final RoomContentService roomContentService;

    @Test
    void save_shouldSaveRoomContent() {
        var roomContentToSave = getRoomContentCreateDto();
        var roomContent = getRoomContent();
        when(roomContentCreateMapper.map(roomContentToSave)).thenReturn(roomContent);
        when(roomContentRepository.save(roomContent)).thenReturn(roomContent);

        var actualRoomContent = roomContentService.save(roomContentToSave);

        assertAll(() -> {
            assertEquals(actualRoomContent.getRoomId(), roomContent.getRoom().getId());
            assertEquals(actualRoomContent.getLink(), roomContent.getLink());
            assertEquals(actualRoomContent.getType(), roomContent.getType().name());
        });
    }

    @Test
    void delete_shouldDeleteRoomContent_whenExists() {
        var existentContent = getRoomContent();
        when(roomContentRepository.findById(EXISTENT_ROOM_CONTENT_ID)).thenReturn(Optional.of(existentContent));

        var expectedResult = roomContentService.delete(EXISTENT_ROOM_CONTENT_ID);

        assertTrue(expectedResult);
        verify(roomContentRepository, times(1)).delete(existentContent);
        verify(roomContentRepository, times(1)).flush();
    }

    @Test
    void delete_shouldReturnFalse_whenRoomContentNotExist() {
        when(roomContentRepository.findById(NON_EXISTENT_ROOM_CONTENT_ID)).thenReturn(Optional.empty());

        var expectedResult = roomContentService.delete(NON_EXISTENT_ROOM_CONTENT_ID);

        assertFalse(expectedResult);
    }

    private RoomContentCreateDto getRoomContentCreateDto() {
        return new RoomContentCreateDto(
                new MockMultipartFile("RoomPhoto.jpg", "RoomPhoto.jpg", "application/octet-stream", new byte[]{}),
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
}