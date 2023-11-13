package com.sspdev.hotelbooking.integration.http.rest;

import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@AutoConfigureMockMvc
class HotelContentRestControllerIT extends IntegrationTestBase {

    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer EXISTENT_CONTENT_ID = 1;
    private static final Integer NON_EXISTENT_CONTENT_ID = 999;

    private final MockMvc mockMvc;

    @Test
    void findContent_shouldFindHotelContent_whenContentExists() throws Exception {
        mockMvc.perform(get("/api/v1/hotels/" + EXISTENT_HOTEL_ID + "/content/" + EXISTENT_CONTENT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }

    @Test
    void findContent_shouldReturnNotFound_whenContentNotExists() throws Exception {
        mockMvc.perform(get("/api/v1/hotels/" + EXISTENT_HOTEL_ID + "/content/" + NON_EXISTENT_CONTENT_ID))
                .andExpect(status().isNotFound());
    }
}