package com.sspdev.hotelbooking.unit.http.rest;

import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.dto.RoomContentReadDto;
import com.sspdev.hotelbooking.http.rest.RoomContentRestController;
import com.sspdev.hotelbooking.service.ApplicationContentService;
import com.sspdev.hotelbooking.service.RoomContentService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@WebMvcTest(controllers = RoomContentRestController.class)
@WithMockUser(username = "test@gmail.com", password = "test", authorities = {"ADMIN", "USER", "OWNER"})
class RoomContentRestControllerTest {

    private static final Integer EXISTENT_ROOM_ID = 1;
    private static final Integer EXISTENT_CONTENT_ID = 1;
    private static final Integer NON_EXISTENT_CONTENT_ID = 999;

    @MockBean
    private final RoomContentService roomContentService;

    @MockBean
    private final ApplicationContentService applicationContentService;

    private final MockMvc mockMvc;

    @Test
    void getImage_shouldReturnImageByRoom_whenImageExists() throws Exception {
        var existentContent = getRoomContentReadDto();
        when(roomContentService.findByRoom(EXISTENT_ROOM_ID)).thenReturn(List.of(existentContent));
        when(applicationContentService.getImage(existentContent.getLink())).thenReturn(Optional.of(new byte[]{0, 0, 0}));

        mockMvc.perform(get("/api/v1/rooms/" + EXISTENT_ROOM_ID + "/content/" + EXISTENT_CONTENT_ID)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_OCTET_STREAM));
    }

    @Test
    void getImage_shouldReturnNotFound_whenImageNotExists() throws Exception {
        when(roomContentService.findByRoom(EXISTENT_ROOM_ID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/rooms/" + EXISTENT_ROOM_ID + "/content/" + NON_EXISTENT_CONTENT_ID)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    private RoomContentReadDto getRoomContentReadDto() {
        return new RoomContentReadDto(
                EXISTENT_CONTENT_ID,
                "testPhoto.jpg",
                ContentType.PHOTO.name(),
                EXISTENT_ROOM_ID
        );
    }
}