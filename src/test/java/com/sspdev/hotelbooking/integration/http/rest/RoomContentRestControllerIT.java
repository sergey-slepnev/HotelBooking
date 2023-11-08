package com.sspdev.hotelbooking.integration.http.rest;

import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@AutoConfigureMockMvc
class RoomContentRestControllerIT extends IntegrationTestBase {

    private static final Integer EXISTENT_ROOM_ID = 1;
    private static final Integer EXISTENT_CONTENT_ID = 1;
    private static final Integer NON_EXISTENT_CONTENT_ID = 999;

    private final MockMvc mockMvc;

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
}