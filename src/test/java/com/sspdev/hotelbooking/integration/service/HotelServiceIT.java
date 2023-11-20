package com.sspdev.hotelbooking.integration.service;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.enums.Star;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.HotelCreateEditDto;
import com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto;
import com.sspdev.hotelbooking.dto.HotelReadDto;
import com.sspdev.hotelbooking.dto.filter.HotelFilter;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import com.sspdev.hotelbooking.service.HotelContentService;
import com.sspdev.hotelbooking.service.HotelDetailsService;
import com.sspdev.hotelbooking.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
public class HotelServiceIT extends IntegrationTestBase {

    private static final Integer NUMBER_HOTELS_IN_RUSSIA = 3;
    private static final Integer NUMBER_PLAZA_HOTELS = 4;
    private static final Integer NUMBER_NO_FILTER_HOTELS = 5;
    private static final Integer FIRST_OWNER_ID = 4;
    private static final Integer SECOND_OWNER_ID = 5;
    private static final Integer NUMBER_OF_FIRST_OWNER_HOTELS = 3;
    private static final Integer NUMBER_OF_SECOND_OWNER_HOTELS = 2;
    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer NON_EXISTENT_HOTEL_ID = 99;

    private final HotelService hotelService;
    private final HotelDetailsService hotelDetailsService;
    private final HotelContentService hotelContentService;

    @ParameterizedTest
    @MethodSource("getArgumentsForFindAllByFilter")
    void findAll_shouldReturnHotelsByFilter(HotelFilter filter, Integer expectedNumberOfHotels) {
        var actualHotels = hotelService.findAll(filter);

        assertEquals(actualHotels.size(), expectedNumberOfHotels);
    }

    static Stream<Arguments> getArgumentsForFindAllByFilter() {
        return Stream.of(
//                country = Russia filter - > 3 hotels
                Arguments.of(HotelFilter.builder()
                                .country("Russia")
                                .build(),
                        NUMBER_HOTELS_IN_RUSSIA),
//                with "plaza" in hotel name filter -> 4 hotels
                Arguments.of(HotelFilter.builder()
                                .name("plaza")
                                .build(),
                        NUMBER_PLAZA_HOTELS),
//                empty filter -> 5 hotels
                Arguments.of(HotelFilter.builder()
                                .build(),
                        NUMBER_NO_FILTER_HOTELS)
        );
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForFindAllByFilterAndPageable")
    void findAllByFilter_shouldFindAllHotelsByFilterAndPageable_sortedByName(HotelFilter filter,
                                                                             Integer expectedNumberOfHotels,
                                                                             String... expectedHotelNames) {
        var sort = Sort.sort(Hotel.class).by(Hotel::getName);
        var pageable = PageRequest.of(0, 20, sort);

        var actualHotels = hotelService.findAllByFilter(filter, pageable);
        var actualHotelNames = actualHotels.stream().map(HotelReadDto::getName).toList();

        assertThat(actualHotels).hasSize(expectedNumberOfHotels);
        assertThat(actualHotelNames).containsExactly(expectedHotelNames);
    }

    static Stream<Arguments> getArgumentsForFindAllByFilterAndPageable() {
        return Stream.of(
//                empty filter -> find all hotels (5)
                Arguments.of(HotelFilter.builder().build(), 5,
                        new String[]{"KievPlaza", "MinskPlaza", "MoscowHotel", "MoscowPlaza", "PiterPlaza"}),
//                country = "Russia" -> find 3 hotels
                Arguments.of(HotelFilter.builder().country("Russia").build(), 3,
                        new String[]{"MoscowHotel", "MoscowPlaza", "PiterPlaza"}),
//                 star = TWO -> find two 2 hotels
                Arguments.of(HotelFilter.builder().star(Star.TWO).build(), 2,
                        new String[]{"MinskPlaza", "MoscowHotel"})
        );
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForFindAllByOwnerId")
    void findAllByOwnerId_shouldFindHotelsByOwner_whenHotelsExixt(Integer ownerId, Integer expectedNumberOfHotelsByUser, String... expectedHotelNames) {
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

    @ParameterizedTest
    @MethodSource("getArgumentsForFindById")
    void findById_shouldFindHotelById_whenHotelExists(Integer hotelId, Object expectedResult) {
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
    void create_shouldCreateNewHotel_whenCreateDtoValid() {
        var hotelCreateEditDto = getHotelCreateEditDto();
        var hotelDetailsCreatedEditDto = getHotelDetailsCreatedEditDto();
        var actualResult = hotelService.create(hotelCreateEditDto, hotelDetailsCreatedEditDto, null);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isPositive();
    }

    @Test
    void update_shouldUpdateExistentHotel_whenUpdateDtoValid() {
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
        updatedHotel.ifPresent(hotel ->
                assertAll(() -> {
                    assertEquals("SibirPlaza", hotel.getName());
                    assertEquals(false, hotel.getAvailable());
                    assertEquals(Status.BLOCKED, hotel.getStatus());
                }));
    }

    @Test
    void delete_shouldDeleteHotelWithHotelDetailsAndContent_whenEverythingExist() {
        var existentHotel = hotelService.findById(EXISTENT_HOTEL_ID);
        var existentHotelDetails = hotelDetailsService.findByHotelId(EXISTENT_HOTEL_ID);
        var existentContent = hotelContentService.findContent(EXISTENT_HOTEL_ID);
        assertThat(existentHotel).isPresent();
        assertThat(existentHotelDetails).isPresent();
        assertThat(existentContent).hasSize(2);

        var actualResult = hotelService.delete(EXISTENT_HOTEL_ID);
        var hotelAfterDeleting = hotelService.findById(EXISTENT_HOTEL_ID);
        var hotelDetailsAfterDeleting = hotelDetailsService.findByHotelId(EXISTENT_HOTEL_ID);
        var contentAfterDeleting = hotelContentService.findContent(EXISTENT_HOTEL_ID);

        assertTrue(actualResult);
        assertThat(hotelAfterDeleting).isEmpty();
        assertThat(hotelDetailsAfterDeleting).isEmpty();
        assertThat(contentAfterDeleting).hasSize(0);
    }

    @Test
    void delete_shouldReturnFalse_whenHotelNotExist() {
        var actualResult = hotelService.delete(NON_EXISTENT_HOTEL_ID);

        assertFalse(actualResult);
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