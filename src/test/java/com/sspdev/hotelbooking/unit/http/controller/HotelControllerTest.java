package com.sspdev.hotelbooking.unit.http.controller;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.database.entity.enums.Star;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.HotelContentReadDto;
import com.sspdev.hotelbooking.dto.HotelDetailsReadDto;
import com.sspdev.hotelbooking.dto.HotelReadDto;
import com.sspdev.hotelbooking.service.HotelContentService;
import com.sspdev.hotelbooking.service.HotelDetailsService;
import com.sspdev.hotelbooking.service.HotelService;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RequiredArgsConstructor
@AutoConfigureMockMvc
@WithMockUser(username = "test@gmail.com", password = "test", authorities = {"ADMIN", "USER", "OWNER"})
public class HotelControllerTest extends UnitTestBase {

    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer NON_EXISTENT_HOTEL_ID = 999;
    private static final Integer EXISTENT_HOTEL_CONTENT_ID = 1;
    private static final Integer EXISTENT_OWNER_ID = 4;
    private static final Integer EXISTENT_HOTEL_DETAILS_ID = 1;

    private final MockMvc mockMvc;

    @MockBean
    private final HotelService hotelService;

    @MockBean
    private final HotelDetailsService hotelDetailsService;

    @MockBean
    private final HotelContentService hotelContentService;

    @Test
    void findById_shouldReturnPageWithHotelAndHotelDetailsAndContent_whenExist() throws Exception {
        var hotel = getHotelReadDto();
        var hotelDetails = getHotelDetailsReadDto();
        var contents = getHotelContentReadDto();

        when(hotelService.findById(EXISTENT_HOTEL_ID)).thenReturn(Optional.of(hotel));
        when(hotelDetailsService.findByHotelId(EXISTENT_HOTEL_ID)).thenReturn(Optional.of(hotelDetails));
        when(hotelContentService.findContent(EXISTENT_HOTEL_ID)).thenReturn(List.of(contents));

        mockMvc.perform(get("/my-booking/hotels/" + EXISTENT_HOTEL_ID)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("hotel", "hotelDetails", "contents"))
                .andExpect(model().size(3))
                .andExpect(model().attribute("contents", hasSize(1)))
                .andExpect(view().name("hotel/hotel"));
    }

    @Test
    void findById_shouldReturnNotFound_whenHotelNotExist() throws Exception {
        when(hotelService.findById(NON_EXISTENT_HOTEL_ID)).thenReturn(Optional.empty());
        when(hotelDetailsService.findByHotelId(NON_EXISTENT_HOTEL_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/my-booking/hotels/" + NON_EXISTENT_HOTEL_ID))
                .andExpect(status().isNotFound());
    }

    private HotelReadDto getHotelReadDto() {
        return new HotelReadDto(
                EXISTENT_HOTEL_ID,
                EXISTENT_OWNER_ID,
                "Hotel",
                true,
                Status.APPROVED
        );
    }

    private HotelDetailsReadDto getHotelDetailsReadDto() {
        return new HotelDetailsReadDto(
                EXISTENT_HOTEL_DETAILS_ID,
                EXISTENT_HOTEL_ID,
                "8-800-300-400",
                "Россия",
                "Самара",
                "Южный район",
                "Новая",
                4,
                Star.ONE,
                "Лучший отель!"
        );
    }

    private Hotel getHotel() {
        return Hotel.builder()
                .id(EXISTENT_HOTEL_ID)
                .build();
    }

    private HotelContentReadDto getHotelContentReadDto() {
        return new HotelContentReadDto(
                EXISTENT_HOTEL_CONTENT_ID,
                "test.jpg",
                ContentType.PHOTO.name(),
                EXISTENT_HOTEL_ID
        );
    }
}