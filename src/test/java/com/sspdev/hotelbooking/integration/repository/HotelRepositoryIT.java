package com.sspdev.hotelbooking.integration.repository;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.enums.Star;
import com.sspdev.hotelbooking.database.repository.HotelRepository;
import com.sspdev.hotelbooking.dto.HotelInfo;
import com.sspdev.hotelbooking.dto.filter.HotelFilter;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
public class HotelRepositoryIT extends IntegrationTestBase {

    private static final Integer FIRST_OWNER_ID = 4;
    private static final Integer SECOND_OWNER_ID = 5;
    private static final Integer FIRST_OWNER_HOTEL_COUNT = 3;
    private static final Integer SECOND_OWNER_HOTEL_COUNT = 2;
    private static final Integer NO_PREDICATE_FILTER_PAGE_SIZE = 5;
    private static final Integer COUNTRY_LOCALITY_STAR_PREDICATE_FILTER_PAGE_SIZE = 1;

    private final HotelRepository hotelRepository;

    @ParameterizedTest
    @MethodSource("getDataForFindHotelByOwnerId")
    void checkFindHotelsByOwnerId(Integer ownerId, Integer hotelsCount, String... expectedHotelNames) {
        var hotelsByOwnerId = hotelRepository.findAllByOwnerId(ownerId);

        assertEquals(hotelsByOwnerId.size(), hotelsCount);

        var actualHotelNames = hotelsByOwnerId.stream().map(Hotel::getName).toList();

        assertThat(actualHotelNames).contains(expectedHotelNames);
    }

    static @NotNull Stream<Arguments> getDataForFindHotelByOwnerId() {
        return Stream.of(
                Arguments.of(FIRST_OWNER_ID, FIRST_OWNER_HOTEL_COUNT, new String[]{"MoscowPlaza", "MoscowHotel", "KievPlaza"}),
                Arguments.of(SECOND_OWNER_ID, SECOND_OWNER_HOTEL_COUNT, new String[]{"PiterPlaza", "MinskPlaza"})
        );
    }

    @Test
    void shouldFindTopFiveByRatingWithDetailsAndFirstPhoto() {
        var actualHotelsInfo = hotelRepository.findTopFiveByRatingWithDetailsAndFirstPhoto();

        var hotelsWithNameAndAvgRating = actualHotelsInfo.stream()
                .collect(Collectors.toMap(HotelInfo::name, HotelInfo::avgRating,
                        (previousRating, newRating) -> newRating, LinkedHashMap::new));

        assertThat(hotelsWithNameAndAvgRating).containsExactly(
                entry("MoscowPlaza", 4.3),
                entry("MoscowHotel", 4.0),
                entry("PiterPlaza", 3.8),
                entry("MinskPlaza", 3.7),
                entry("KievPlaza", 3.3));
    }

    @ParameterizedTest
    @MethodSource("getDataForFindByFilterMethod")
    void checkFindByFilter(HotelFilter filter, Integer expectedHotelCollectionSize, String... expectedHotelsNames) {
        var actualHotels = hotelRepository.findAllByFilter(filter);

        var actualHotelNames = actualHotels.stream().map(Hotel::getName).toList();

        assertThat(actualHotelNames).hasSize(expectedHotelCollectionSize);
        assertThat(actualHotelNames).containsExactly(expectedHotelsNames);
    }

    static Stream<Arguments> getDataForFindByFilterMethod() {
        return Stream.of(
//                no-predicate filter
                Arguments.of(
                        HotelFilter.builder().build(),
                        NO_PREDICATE_FILTER_PAGE_SIZE,
                        new String[]{"PiterPlaza", "MoscowPlaza", "KievPlaza", "MoscowHotel", "MinskPlaza"}
                ),
//                Russia-Moscow_FourStar predicate filter
                Arguments.of(HotelFilter.builder()
                                .country("Russia")
                                .locality("Moscow")
                                .star(Star.FOUR)
                                .build(),
                        COUNTRY_LOCALITY_STAR_PREDICATE_FILTER_PAGE_SIZE,
                        new String[]{"MoscowPlaza"}
                )
        );
    }
}