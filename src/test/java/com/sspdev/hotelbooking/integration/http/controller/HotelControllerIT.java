package com.sspdev.hotelbooking.integration.http.controller;

import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RequiredArgsConstructor
@AutoConfigureMockMvc
class HotelControllerIT extends IntegrationTestBase {

    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer NON_EXISTENT_HOTEL_ID = 999;

    private final MockMvc mockMvc;

    @Test
    void findById_shouldReturnPageWithHotelAndHotelDetailsAndContent_whenExist() throws Exception {
        mockMvc.perform(get("/my-booking/hotels/" + EXISTENT_HOTEL_ID)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("hotel", "hotelDetails", "contents"))
                .andExpect(model().size(3))
                .andExpect(model().attribute("contents", hasSize(2)))
                .andExpect(view().name("hotel/hotel"));
    }

    @Test
    void findById_shouldReturnNotFound_whenHotelNotExist() throws Exception {
        mockMvc.perform(get("/my-booking/hotels/" + NON_EXISTENT_HOTEL_ID))
                .andExpect(status().isNotFound());
    }
}