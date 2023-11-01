package com.sspdev.hotelbooking.integration.service;

import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.dto.RoomContentCreateDto;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import com.sspdev.hotelbooking.service.RoomContentService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
class RoomContentServiceIT extends IntegrationTestBase {

    private static final Integer EXISTENT_ROOM_ID = 1;

    private final RoomContentService roomContentService;

    @Test
    void save_shouldSaveRoomContent() {
        var contentToSave = getRoomContentCreateDto();
        var actualRoomContent = roomContentService.save(contentToSave);

        assertAll(() -> {
                    assertEquals(actualRoomContent.getRoomId(), contentToSave.getRoomId());
                    assertEquals(actualRoomContent.getLink(), contentToSave.getContent().getOriginalFilename());
                    assertEquals(actualRoomContent.getType(), contentToSave.getContentType().name());
                }
        );
    }

    private RoomContentCreateDto getRoomContentCreateDto() {
        return new RoomContentCreateDto(
                new MockMultipartFile("RoomPhoto.jpg", "RoomPhoto.jpg", "application/octet-stream", new byte[]{}),
                ContentType.PHOTO,
                EXISTENT_ROOM_ID
        );
    }
}