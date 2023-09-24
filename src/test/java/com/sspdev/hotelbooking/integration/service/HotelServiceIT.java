package com.sspdev.hotelbooking.integration.service;

import com.sspdev.hotelbooking.database.entity.enums.Star;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.HotelCreateEditDto;
import com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto;
import com.sspdev.hotelbooking.dto.HotelReadDto;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import com.sspdev.hotelbooking.service.HotelDetailsService;
import com.sspdev.hotelbooking.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
public class HotelServiceIT extends IntegrationTestBase {

    private static final Integer FIRST_OWNER_ID = 4;
    private static final Integer SECOND_OWNER_ID = 5;
    private static final Integer NUMBER_OF_FIRST_OWNER_HOTELS = 3;
    private static final Integer NUMBER_OF_SECOND_OWNER_HOTELS = 2;
    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer NON_EXISTENT_HOTEL_ID = 99;

    private final HotelService hotelService;
    private final HotelDetailsService hotelDetailsService;

    @ParameterizedTest
    @MethodSource("getArgumentsForFindAllByOwnerId")
    void checkFindAllByOwnerId(Integer ownerId, Integer expectedNumberOfHotelsByUser, String... expectedHotelNames) {
        var actualHotels = hotelService.findAllByOwnerId(ownerId);
        var actualHotelNames = actualHotels.stream().map(HotelReadDto::getName).toList();

        assertThat(actualHotels).hasSize(expectedNumberOfHotelsByUser);
        assertThat(actualHotelNames).contains(expectedHotelNames);
    }

    static Stream<Arguments> getArgumentsForFindAllByOwnerId() {
        return Stream.of(
                Arguments.of(FIRST_OWNER_ID,
                        NUMBER_OF_FIRST_OWNER_HOTELS,
                        new String[]{"MoscowPlaza", "MoscowHotel", "KievPlaza"}),
                Arguments.of(SECOND_OWNER_ID,
                        NUMBER_OF_SECOND_OWNER_HOTELS,
                        new String[]{"PiterPlaza", "MinskPlaza"})
        );
    }

    @Test
    void shouldReturnAllHotels() {
        var allHotels = hotelService.findAll();

        assertThat(allHotels).hasSize(5);
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForFindById")
    void checkFindById(Integer hotelId, Object expectedResult) {
        var actualResult = hotelService.findById(hotelId);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    static Stream<Arguments> getArgumentsForFindById() {
        return Stream.of(
                Arguments.of(EXISTENT_HOTEL_ID, Optional.of(
                        new HotelReadDto(
                                EXISTENT_HOTEL_ID,
                                FIRST_OWNER_ID,
                                "MoscowPlaza",
                                true,
                                Status.APPROVED))),
                Arguments.of(NON_EXISTENT_HOTEL_ID, Optional.empty())
        );
    }

    @Test
    void shouldCreateNewHotel() {
        var hotelCreateEditDto = getHotelCreateEditDto();
        var hotelDetailsCreatedEditDto = getHotelDetailsCreatedEditDto();
        var actualResult = hotelService.create(hotelCreateEditDto, hotelDetailsCreatedEditDto);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isPositive();
    }

    @Test
    void shouldUpdateExistentHotel() {
        var nonUpdatedHotel = hotelService.findById(EXISTENT_HOTEL_ID);
        nonUpdatedHotel.ifPresent(hotel -> {
            assertAll(() -> {
                assertEquals("MoscowPlaza", hotel.getName());
                assertEquals(true, hotel.getAvailable());
                assertEquals(Status.APPROVED, hotel.getStatus());
            });
        });

        var hotelCreateEditDto = getHotelCreateEditDto();
        var hotelDetailsCreatedEditDto = getHotelDetailsCreatedEditDto();

        var updatedHotel = hotelService.update(EXISTENT_HOTEL_ID, hotelCreateEditDto, hotelDetailsCreatedEditDto);
        updatedHotel.ifPresent(hotel -> {
            assertAll(() -> {
                assertEquals("SibirPlaza", hotel.getName());
                assertEquals(false, hotel.getAvailable());
                assertEquals(Status.BLOCKED, hotel.getStatus());
            });
        });
    }

    private HotelCreateEditDto getHotelCreateEditDto() {
        return new HotelCreateEditDto(
                FIRST_OWNER_ID,
                "SibirPlaza",
                false,
                Status.BLOCKED
        );
    }

    private HotelDetailsCreateEditDto getHotelDetailsCreatedEditDto() {
        return new HotelDetailsCreateEditDto(
                EXISTENT_HOTEL_ID,
                "+7-965-78-78-999",
                "Russia",
                "Moscow",
                "West",
                "First",
                3,
                Star.FIVE,
                "good hotel"
        );
    }
}