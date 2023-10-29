package com.sspdev.hotelbooking.unit.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import com.sspdev.hotelbooking.dto.PageResponse;
import com.sspdev.hotelbooking.dto.RoomReadDto;
import com.sspdev.hotelbooking.dto.filter.RoomFilter;
import com.sspdev.hotelbooking.http.controller.RoomController;
import com.sspdev.hotelbooking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@WebMvcTest(value = RoomController.class)
@WithMockUser(username = "test@gmail.com", password = "test", authorities = {"USER", "OWNER", "ADMIN"})
public class RoomControllerTest {

    public static final Integer EXISTENT_ROOM_ID = 1;
    public static final Integer NON_EXISTENT_ROOM_ID = 999;

    private final MockMvc mockMvc;

    @MockBean
    private final RoomService roomService;

    @MockBean
    private final Page<RoomReadDto> roomReadDtoPage;

    @Test
    void findById_shouldFindRoomById_whenRoomExists() throws Exception {
        var expectedRoomDto = getRoomReadDto();
        when(roomService.findById(EXISTENT_ROOM_ID)).thenReturn(Optional.of(expectedRoomDto));

        mockMvc.perform(get("/my-booking/rooms/" + EXISTENT_ROOM_ID))
                .andExpect(status().isOk())
                .andExpect(model().attribute("room", expectedRoomDto));
    }

    @Test
    void findById_shouldReturnNotFound_whenRoomNotExist() throws Exception {
        when(roomService.findById(NON_EXISTENT_ROOM_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/my-booking/rooms/" + EXISTENT_ROOM_ID))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 5L, 20L, 100L})
    void findAll_shouldFindAllRooms_whenNoPredicatesFilter(long expectedTotalElements) throws Exception {
        var pageMetadata = new PageResponse.Metadata(0, 20, expectedTotalElements);
        var expectedResponseContent = List.of(getRoomReadDto());
        var pageResponse = new PageResponse<>(expectedResponseContent, pageMetadata);

        when(roomReadDtoPage.getContent()).thenReturn(expectedResponseContent);
        when(roomReadDtoPage.getSize()).thenReturn(20);
        when(roomReadDtoPage.getTotalElements()).thenReturn(expectedTotalElements);
        when(roomService.findAll(any(RoomFilter.class), any(Pageable.class))).thenReturn(roomReadDtoPage);

        var mvcResult = mockMvc.perform(get("/my-booking/rooms/search"))
                .andExpectAll(
                        status().isOk(),
                        model().attributeExists("rooms", "filter"),
                        model().attribute("rooms", pageResponse)
                ).andReturn();

        var stringResult = mvcResult.getModelAndView().getModel().get("rooms").toString();

        assertThat(stringResult).contains("totalElements=" + expectedTotalElements);
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
