package com.sspdev.hotelbooking.unit.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.HotelReadDto;
import com.sspdev.hotelbooking.dto.PageResponse;
import com.sspdev.hotelbooking.dto.RoomCreateEditDto;
import com.sspdev.hotelbooking.dto.RoomReadDto;
import com.sspdev.hotelbooking.dto.UserReadDto;
import com.sspdev.hotelbooking.dto.filter.RoomFilter;
import com.sspdev.hotelbooking.service.HotelService;
import com.sspdev.hotelbooking.service.RoomService;
import com.sspdev.hotelbooking.service.UserService;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RequiredArgsConstructor
@AutoConfigureMockMvc
@WithMockUser(username = "test@gmail.com", password = "test", authorities = {"USER", "OWNER", "ADMIN"})
public class RoomControllerTest extends UnitTestBase {

    public static final Integer EXISTENT_ROOM_ID = 1;
    public static final Integer NOT_EXISTENT_ROOM_ID = 999;
    private static final Integer EXISTENT_OWNER_ID = 4;
    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer NOT_EXISTENT_OWNER_ID = 999;
    private static final Integer NOT_EXISTENT_HOTEL_ID = 999;

    private final MockMvc mockMvc;

    @MockBean
    private final RoomService roomService;

    @MockBean
    private final Page<RoomReadDto> roomReadDtoPage;

    @MockBean
    private final HotelService hotelService;

    @MockBean
    private final UserService userService;

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
        when(roomService.findById(NOT_EXISTENT_ROOM_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/my-booking/rooms/" + EXISTENT_ROOM_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturnAddRoomPage() throws Exception {
        var existentOwner = getUserReadDto();
        var existentHotel = getHotelReadDto();
        when(userService.findById(EXISTENT_OWNER_ID)).thenReturn(Optional.of(existentOwner));
        when(hotelService.findById(EXISTENT_HOTEL_ID)).thenReturn(Optional.of(existentHotel));

        mockMvc.perform(get("/my-booking/rooms/" + EXISTENT_OWNER_ID + "/" + EXISTENT_HOTEL_ID + "/add")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("room/add"))
                .andExpect(model().attributeExists("hotel", "types", "stars", "contentTypes"));
    }

    @Test
    void create_shouldReturnNotFound_whenUserAndHotelNotExist() throws Exception {
        when(userService.findById(NOT_EXISTENT_OWNER_ID)).thenReturn(Optional.empty());
        when(hotelService.findById(NOT_EXISTENT_HOTEL_ID)).thenReturn(Optional.empty());
        mockMvc.perform(get("/my-booking/rooms/" + NOT_EXISTENT_OWNER_ID + "/" + NOT_EXISTENT_HOTEL_ID + "/add"))
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

    @Test
    void findByHotel_shouldFindRoomsByHotelPage_whenHotelAndRoomsExist() throws Exception {
        var room = getRoomReadDto();
        when(roomService.findByHotel(EXISTENT_HOTEL_ID)).thenReturn(List.of(room, room));

        mockMvc.perform(get("/my-booking/rooms/" + EXISTENT_HOTEL_ID + "/rooms-by-hotel"))
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("rooms"))
                .andExpect(model().attribute("rooms", hasSize(2)))
                .andExpect(view().name("room/rooms-by-hotel"));
    }

    @Test
    void findByHotel_shouldReturnRoomsByHotelPageWithNotRooms() throws Exception {
        when(roomService.findByHotel(NOT_EXISTENT_HOTEL_ID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/my-booking/rooms/" + NOT_EXISTENT_HOTEL_ID + "/rooms-by-hotel"))
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("rooms"))
                .andExpect(model().attribute("rooms", hasSize(0)))
                .andExpect(view().name("room/rooms-by-hotel"));
    }

    @Test
    void delete_shouldDeleteAndRedirectToHotelPage_whenRoomExists() throws Exception {
        var hotelInSession = getHotelReadDto();
        var existentRoom = getRoomReadDto();

        when(hotelService.findById(EXISTENT_HOTEL_ID)).thenReturn(Optional.of(hotelInSession));
        when(roomService.findById(EXISTENT_ROOM_ID)).thenReturn(Optional.of(existentRoom));
        when(roomService.delete(EXISTENT_ROOM_ID)).thenReturn(true);

        mockMvc.perform(post("/my-booking/rooms/" + EXISTENT_ROOM_ID + "/delete")
                        .sessionAttr("hotel", hotelInSession)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/my-booking/hotels/{d\\+}"));
    }

    @Test
    void delete_shouldReturnNotFound_whenRoomNotExist() throws Exception {
        var hotelInSession = getHotelReadDto();
        when(hotelService.findById(EXISTENT_HOTEL_ID)).thenReturn(Optional.of(hotelInSession));
        when(roomService.findById(EXISTENT_ROOM_ID)).thenReturn(Optional.empty());
        when(roomService.delete(EXISTENT_ROOM_ID)).thenReturn(false);

        mockMvc.perform(post("/my-booking/rooms/" + NOT_EXISTENT_ROOM_ID + "/delete")
                        .sessionAttr("hotel", hotelInSession)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_shouldReturnEditRoomPage_whenRoomExist() throws Exception {
        var room = getRoomReadDto();
        when(roomService.findById(EXISTENT_ROOM_ID)).thenReturn(Optional.of(room));

        mockMvc.perform(get("/my-booking/rooms/" + EXISTENT_ROOM_ID + "/update")
                        .sessionAttr("room", room))
                .andExpect(status().isOk())
                .andExpect(model().attribute("types", RoomType.values()))
                .andExpect(view().name("room/edit"));
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

    private HotelReadDto getHotelReadDto() {
        return new HotelReadDto(
                EXISTENT_HOTEL_ID,
                EXISTENT_OWNER_ID,
                "MoscowPlaza",
                true,
                Status.APPROVED);
    }

    private UserReadDto getUserReadDto() {
        return new UserReadDto(
                4,
                Role.USER,
                "user",
                "123",
                "Petr",
                "Petrov",
                LocalDate.of(2000, 10, 10),
                "+7-954-984-98-98",
                "user_avatar",
                Status.NEW,
                LocalDateTime.of(2023, 5, 5, 10, 10));
    }

    private RoomCreateEditDto getRoomEditDto() {
        return new RoomCreateEditDto(
                EXISTENT_HOTEL_ID,
                1,
                RoomType.TRPL,
                50.5,
                2,
                1,
                BigDecimal.valueOf(2500),
                1,
                true,
                "Просторная комната",
                null
        );
    }
}