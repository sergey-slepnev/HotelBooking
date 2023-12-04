package com.sspdev.hotelbooking.integration.http.controller;

import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@AutoConfigureMockMvc
class BookingRequestControllerIT extends IntegrationTestBase {

    private static final Long EXISTENT_REQUEST_ID = 1L;
    private static final Long NOT_EXISTENT_REQUEST_ID = 999L;

    private final MockMvc mockMvc;

    @Test
    void findById_shouldFindBookingRequest_whenExist() throws Exception {
        mockMvc.perform(get("/my-booking/booking-requests/" + EXISTENT_REQUEST_ID))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("request", "statuses"));
    }

    @Test
    void findById_shouldReturn404_whenRequestNotExist() throws Exception {
        mockMvc.perform(get("/my-booking/booking-requests/" + NOT_EXISTENT_REQUEST_ID))
                .andExpect(status().isNotFound());
    }
}