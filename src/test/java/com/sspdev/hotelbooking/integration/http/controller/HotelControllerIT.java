package com.sspdev.hotelbooking.integration.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.Star;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import com.sspdev.hotelbooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static com.sspdev.hotelbooking.dto.filter.HotelFilter.Fields.country;
import static com.sspdev.hotelbooking.dto.filter.HotelFilter.Fields.star;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
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
    private static final Integer EXISTENT_OWNER_ID = 4;

    private final MockMvc mockMvc;
    private final UserService userService;

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

    @ParameterizedTest
    @MethodSource("getArgumentsForFindAll")
    void findAll_shouldFindHotelsByFilter(String paramName, String paramValue, Integer expectedNumbersOfHotels) throws Exception {
        var mvcResult = mockMvc.perform(get("/my-booking/hotels")
                        .queryParam(paramName, paramValue))
                .andExpectAll(
                        status().isOk(),
                        model().attributeExists("hotels", "stars", "filter", "countries"))
                .andReturn();
        var hotelsToString = requireNonNull(mvcResult.getModelAndView()).getModel().get("hotels").toString();

        assertThat(hotelsToString).contains("totalElements=" + expectedNumbersOfHotels);
    }

    @Test
    void add_shouldReturnAddHotelPage() throws Exception {
        var existentOwner = userService.findById(EXISTENT_OWNER_ID);
        mockMvc.perform(get("/my-booking/hotels/" + EXISTENT_OWNER_ID + "/add-hotel")
                        .sessionAttr("user", existentOwner.get())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("hotelCreateDto", "hotelDetails", "hotelContent", "stars"))
                .andExpect(view().name("hotel/add"));
    }

    static Stream<Arguments> getArgumentsForFindAll() {
        return Stream.of(
                Arguments.of(star, "", 5),
                Arguments.of(star, Star.TWO.name(), 2),
                Arguments.of(country, "Russia", 3)
        );
    }
}