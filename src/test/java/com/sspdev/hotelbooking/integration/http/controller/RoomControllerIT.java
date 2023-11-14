package com.sspdev.hotelbooking.integration.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import com.sspdev.hotelbooking.dto.RoomReadDto;
import com.sspdev.hotelbooking.dto.filter.RoomFilter;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static com.sspdev.hotelbooking.dto.RoomCreateEditDto.Fields.adultBedCount;
import static com.sspdev.hotelbooking.dto.RoomCreateEditDto.Fields.available;
import static com.sspdev.hotelbooking.dto.RoomCreateEditDto.Fields.childrenBedCount;
import static com.sspdev.hotelbooking.dto.RoomCreateEditDto.Fields.cost;
import static com.sspdev.hotelbooking.dto.RoomCreateEditDto.Fields.floor;
import static com.sspdev.hotelbooking.dto.RoomCreateEditDto.Fields.hotelId;
import static com.sspdev.hotelbooking.dto.RoomCreateEditDto.Fields.roomNo;
import static com.sspdev.hotelbooking.dto.RoomCreateEditDto.Fields.square;
import static com.sspdev.hotelbooking.dto.RoomCreateEditDto.Fields.type;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RequiredArgsConstructor
@AutoConfigureMockMvc(addFilters = false)
class RoomControllerIT extends IntegrationTestBase {

    private static final Integer EXISTENT_ROOM_ID = 1;
    private static final Integer NON_EXISTENT_ROOM_ID = 999;
    private static final Integer ALL_ROOMS = 16;
    private static final Integer COST_FROM_ROOMS_NUMBER = 13;
    private static final Integer EXISTENT_OWNER_ID = 4;
    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer NON_EXISTENT_OWNER_ID = 999;
    private static final Integer NON_EXISTENT_HOTEL_ID = 999;

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

    @ParameterizedTest
    @MethodSource("getArgumentsForFindAllByFilter")
    void findAll_shouldFindRooms_withCostFromFilter(String paramName, String paramValue, Integer expectedNumbersOfRooms) throws Exception {
        var mvcResult = mockMvc.perform(get("/my-booking/rooms/search").queryParam(paramName, paramValue))
                .andExpect(status().isOk())
                .andExpect(view().name("room/search"))
                .andExpect(model().attributeExists("rooms"))
                .andReturn();

        var stringResult = requireNonNull(mvcResult.getModelAndView()).getModel().get("rooms").toString();

        assertThat(stringResult).contains("totalElements=" + expectedNumbersOfRooms);
    }

    static Stream<Arguments> getArgumentsForFindAllByFilter() {
        return Stream.of(
                Arguments.of(RoomFilter.Fields.costFrom, "", ALL_ROOMS),
                Arguments.of(RoomFilter.Fields.costFrom, "1100", COST_FROM_ROOMS_NUMBER
                ));
    }

    @Test
    void create_shouldReturnAddRoomPage() throws Exception {
        mockMvc.perform(get("/my-booking/rooms/" + EXISTENT_OWNER_ID + "/" + EXISTENT_HOTEL_ID + "/add")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("room/add"))
                .andExpect(model().attributeExists("hotel", "types", "stars", "contentTypes"));
    }

    @Test
    void create_shouldReturnNotFound_whenUserAndHotelNotExist() throws Exception {
        mockMvc.perform(get("/my-booking/rooms" + NON_EXISTENT_OWNER_ID + "/" + NON_EXISTENT_HOTEL_ID + "/add"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_postShouldCreateNewRoom_whenRoomCreateEditDtoValid() throws Exception {
        mockMvc.perform(post("/my-booking/rooms/" + EXISTENT_OWNER_ID + "/" + EXISTENT_HOTEL_ID + "/create")
                        .with(csrf())
                        .param(hotelId, "1")
                        .param(roomNo, "1")
                        .param(type, RoomType.DBL.name())
                        .param(square, "50")
                        .param(adultBedCount, "4")
                        .param(childrenBedCount, "1")
                        .param(cost, "550")
                        .param(floor, "3")
                        .param(available, "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/my-booking/rooms/{\\d+}"))
                .andExpect(flash().attributeCount(0));
    }

    @Test
    void findByHotel_shouldFindRoomsByHotelPage_whenHotelAndRoomsExist() throws Exception {
        mockMvc.perform(get("/my-booking/rooms/" + EXISTENT_HOTEL_ID + "/rooms-by-hotel"))
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("rooms"))
                .andExpect(model().attribute("rooms", hasSize(4)))
                .andExpect(view().name("room/rooms-by-hotel"));
    }

    @Test
    void findByHotel_shouldReturnRoomsByHotelPageWithNotRooms() throws Exception {
        mockMvc.perform(get("/my-booking/rooms/" + NON_EXISTENT_HOTEL_ID + "/rooms-by-hotel"))
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("rooms"))
                .andExpect(model().attribute("rooms", hasSize(0)))
                .andExpect(view().name("room/rooms-by-hotel"));
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