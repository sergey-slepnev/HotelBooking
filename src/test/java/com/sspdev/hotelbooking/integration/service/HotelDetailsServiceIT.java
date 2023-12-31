package com.sspdev.hotelbooking.integration.service;

import com.sspdev.hotelbooking.database.entity.enums.Star;
import com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import com.sspdev.hotelbooking.service.HotelDetailsService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
public class HotelDetailsServiceIT extends IntegrationTestBase {

    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer EXISTENT_HOTEL_DETAILS_ID = 1;
    private static final Integer NOT_EXISTENT_HOTEL_DETAILS_ID = 999;

    private final HotelDetailsService hotelDetailsService;

    @Test
    void shouldFindByHotelId() {
        var actualHotelDetails = hotelDetailsService.findByHotelId(EXISTENT_HOTEL_ID);

        assertThat(actualHotelDetails).isPresent();
        actualHotelDetails.ifPresent(hotelDetails ->
                assertAll(() -> {
                    assertEquals(hotelDetails.getHotelId(), EXISTENT_HOTEL_ID);
                    assertEquals(hotelDetails.getPhoneNumber(), "1111-111-111");
                    assertEquals(hotelDetails.getCountry(), "Russia");
                    assertEquals(hotelDetails.getLocality(), "Moscow");
                    assertEquals(hotelDetails.getFloorCount(), 15);
                    assertEquals(hotelDetails.getStar(), Star.FOUR);
                }));
    }

    @Test
    void shouldCreateNewHotelDetails() {
        var hotelDetailsCreatedEditDto = getHotelDetailsCreatedEditDto();
        var actualHotelDetails = hotelDetailsService.create(hotelDetailsCreatedEditDto);

        assertThat(actualHotelDetails).isNotNull();
        assertThat(actualHotelDetails.getId()).isPositive();
    }

    @Test
    void shouldUpdateExistentHotelDetails() {
        var hotelDetailsToUpdate = getHotelDetailsCreatedEditDto();
        var hotelDetailsBeforeUpdate = hotelDetailsService.findByHotelId(EXISTENT_HOTEL_ID);

        hotelDetailsBeforeUpdate.ifPresent(hotelDetails ->
                assertAll(() -> {
                    assertEquals(hotelDetails.getHotelId(), EXISTENT_HOTEL_ID);
                    assertEquals(hotelDetails.getPhoneNumber(), "1111-111-111");
                    assertEquals(hotelDetails.getCountry(), "Russia");
                    assertEquals(hotelDetails.getLocality(), "Moscow");
                    assertEquals(hotelDetails.getFloorCount(), 15);
                    assertEquals(hotelDetails.getStar(), Star.FOUR);
                }));

        var hotelDetailsAfterUpdate = hotelDetailsService.update(EXISTENT_HOTEL_DETAILS_ID, hotelDetailsToUpdate);

        assertThat(hotelDetailsAfterUpdate).isPresent();
        hotelDetailsAfterUpdate.ifPresent(hotelDetails ->
                assertAll(() -> {
                    assertEquals(hotelDetails.getHotelId(), EXISTENT_HOTEL_ID);
                    assertEquals(hotelDetails.getPhoneNumber(), "+7-965-78-78-888");
                    assertEquals(hotelDetails.getCountry(), "Россия");
                    assertEquals(hotelDetails.getLocality(), "Москва");
                    assertEquals(hotelDetails.getArea(), "Центральный");
                    assertEquals(hotelDetails.getStreet(), "Новокузнецкая");
                    assertEquals(hotelDetails.getStar(), Star.FIVE);
                }));
    }

    @Test
    void findCountries_shouldFindThreeCounties() {
        var countries = hotelDetailsService.findCountries();

        assertThat(countries).hasSize(3);
        assertThat(countries).contains("Russia", "Ukraine", "Belarus");
    }

    @Test
    void delete_shouldDeleteHotelDetailsByHotel_whenExists() {
        var existentHotelDetails = hotelDetailsService.findByHotelId(EXISTENT_HOTEL_ID);
        assertThat(existentHotelDetails).isPresent();

        var isDeleted = hotelDetailsService.delete(EXISTENT_HOTEL_ID);
        var hotelDetailsAfterDeleting = hotelDetailsService.findByHotelId(EXISTENT_HOTEL_ID);

        assertThat(isDeleted).isTrue();
        assertThat(hotelDetailsAfterDeleting).isEmpty();
    }

    @Test
    void delete_shouldReturnFalse_whenHotelDetailsNotExist() {
        var isDeleted = hotelDetailsService.delete(NOT_EXISTENT_HOTEL_DETAILS_ID);

        assertThat(isDeleted).isFalse();
    }

    private HotelDetailsCreateEditDto getHotelDetailsCreatedEditDto() {
        return new HotelDetailsCreateEditDto(
                EXISTENT_HOTEL_ID,
                "+7-965-78-78-888",
                "Россия",
                "Москва",
                "Центральный",
                "Новокузнецкая",
                3,
                Star.FIVE,
                "Очень хороший отель"
        );
    }
}