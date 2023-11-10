package com.sspdev.hotelbooking.integration.http.rest;

import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import com.sspdev.hotelbooking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static com.sspdev.hotelbooking.dto.RoomContentCreateDto.Fields.roomId;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@AutoConfigureMockMvc
class RoomContentRestControllerIT extends IntegrationTestBase {

    private static final Integer EXISTENT_ROOM_ID = 1;
    private static final Integer EXISTENT_CONTENT_ID = 1;
    private static final Integer NON_EXISTENT_CONTENT_ID = 999;

    private final MockMvc mockMvc;
    private final RoomService roomService;

    @Test
    void getImage_shouldReturnImageByRoom_whenImageExists() throws Exception {
        mockMvc.perform(get("/api/v1/rooms/" + EXISTENT_ROOM_ID + "/content/" + EXISTENT_CONTENT_ID)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_OCTET_STREAM));
    }

    @Test
    void getImage_shouldReturnNotFound_whenImageNotExists() throws Exception {
        mockMvc.perform(get("/api/v1/rooms/" + EXISTENT_ROOM_ID + "/content/" + NON_EXISTENT_CONTENT_ID)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldDeleteContentAndRedirectToRoomPage_whenContentExists() throws Exception {
        var roomInSession = roomService.findById(EXISTENT_ROOM_ID);
        mockMvc.perform(post("/api/v1/rooms/content/" + EXISTENT_CONTENT_ID + "/delete")
                        .sessionAttr("room", roomInSession.get())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/my-booking/rooms/{d\\+}"));
    }

    @Test
    void delete_shouldReturnNotFound_whenContentNotExist() throws Exception {
        var roomInSession = roomService.findById(EXISTENT_ROOM_ID);
        mockMvc.perform(post("/api/v1/rooms/content/" + NON_EXISTENT_CONTENT_ID + "/delete")
                        .sessionAttr("room", roomInSession.get())
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldCreateNewContent_whenContentExists() throws Exception {
        var content = new MockMultipartFile(
                "content",
                "first_room_10.jpg",
                APPLICATION_OCTET_STREAM_VALUE,
                new byte[]{0, 0, 0, 0});

        mockMvc.perform(multipart("/api/v1/rooms/" + EXISTENT_ROOM_ID + "/content/create")
                        .file(content)
                        .param(roomId, String.valueOf(EXISTENT_ROOM_ID)))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/my-booking/rooms/{d\\+}"));
    }

    @Test
    void create_shouldJustRedirectToRoomPage_whenContentEmpty() throws Exception {
        var content = new MockMultipartFile(
                "content",
                new byte[]{});

        mockMvc.perform(multipart("/api/v1/rooms/" + EXISTENT_ROOM_ID + "/content/create")
                        .file(content)
                        .param(roomId, String.valueOf(EXISTENT_ROOM_ID)))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/my-booking/rooms/{d\\+}"));
    }
}