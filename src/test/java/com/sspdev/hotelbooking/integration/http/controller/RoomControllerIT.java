package com.sspdev.hotelbooking.integration.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import com.sspdev.hotelbooking.dto.RoomReadDto;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RequiredArgsConstructor
@AutoConfigureMockMvc(addFilters = false)
class RoomControllerIT extends IntegrationTestBase {

    private static final Integer EXISTENT_ROOM_ID = 1;
    private static final Integer NON_EXISTENT_ROOM_ID = 999;

    private final MockMvc mockMvc;

    @Test
    void findById_shouldFindRoomById_whenRoomExists() throws Exception {
        mockMvc.perform(get("/my-booking/rooms/" + EXISTENT_ROOM_ID)
                        .with(csrf()))
                .andExpectAll(
                        status().isOk(),
                        model().attributeExists("room"),
                        model().attribute("room", getRoomReadDto()),
                        view().name("room/room"));
    }

    @Test
    void findById_shouldReturnNotFound_whenRoomNotExist() throws Exception {
        mockMvc.perform(get("/my-booking/rooms/" + NON_EXISTENT_ROOM_ID)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult ->
                        requireNonNull(mvcResult.getResolvedException()).getClass()
                                .equals(ResponseStatusException.class));
    }

    private RoomReadDto getRoomReadDto() {
        return new RoomReadDto(
                EXISTENT_ROOM_ID,
                1,
                1,
                RoomType.TRPL,
                25.3,
                3,
                0,
                BigDecimal.valueOf(1500.99),
                1,
                true,
                "Nice room in moscowPlaza",
                null
        );
    }
}