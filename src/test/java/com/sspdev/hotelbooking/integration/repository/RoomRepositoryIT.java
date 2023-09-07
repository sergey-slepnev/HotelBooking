package com.sspdev.hotelbooking.integration.repository;

import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.database.repository.RoomRepository;
import com.sspdev.hotelbooking.dto.filter.RoomFilter;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class RoomRepositoryIT extends IntegrationTestBase {

    private static final Integer NO_PREDICATE_PAGE_SIZE = 16;
    private static final Integer PREDICATE_PAGE_SIZE = 5;
    private static final BigDecimal MIN_ROOM_COST = BigDecimal.valueOf(1900);
    private static final BigDecimal MAX_ROOM_COST = BigDecimal.valueOf(2100);

    private final RoomRepository roomRepository;

    @ParameterizedTest
    @MethodSource("getDataForFindAllByFilterMethod")
    void checkFindAllByFilter(RoomFilter filter, Integer expectedCollectionSize, Double... expectedSquares) {
        var actualRooms = roomRepository.findAllByFilter(filter);

        var actualSquares = actualRooms.stream()
                .map(Room::getSquare).toList();

        assertThat(actualSquares).hasSize(expectedCollectionSize);
        assertThat(actualSquares).contains(expectedSquares);
    }

    static Stream<Arguments> getDataForFindAllByFilterMethod() {
        return Stream.of(
//                no-predicate filter
                Arguments.of(RoomFilter.builder().build(),
                        NO_PREDICATE_PAGE_SIZE,
                        new Double[]{
                                25.3, 45.0, 35.5, 20.5, 55.5, 15.0, 35.5, 23.5,
                                25.5, 45.0, 35.5, 20.5, 25.5, 45.0, 35.5, 20.5
                        }),
//                predicate filter
                Arguments.of(RoomFilter.builder()
                                .available(true)
                                .costFrom(MIN_ROOM_COST)
                                .costTo(MAX_ROOM_COST)
                                .build(),
                        PREDICATE_PAGE_SIZE,
                        new Double[]{25.5, 35.5, 35.5, 35.5, 45.0})
        );
    }
}