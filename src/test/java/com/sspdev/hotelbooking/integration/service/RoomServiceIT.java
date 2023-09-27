package com.sspdev.hotelbooking.integration.service;

import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.dto.RoomReadDto;
import com.sspdev.hotelbooking.dto.filter.RoomFilter;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import com.sspdev.hotelbooking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
public class RoomServiceIT extends IntegrationTestBase {

    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Long NUMBER_OF_ROOMS_WITH_NO_PREDICATE = 16L;
    private static final Long NUMBER_OF_ROOMS_WITH_COST_FILTER = 5L;

    private final RoomService roomService;

    @Test
    void findById_shouldFindRoomById_whenRoomExist() {
        var maybeRoom = roomService.findById(EXISTENT_HOTEL_ID);

        assertThat(maybeRoom).isPresent();
        maybeRoom.ifPresent(room ->
                assertAll(() -> {
                    assertEquals(room.getRoomNo(), 1);
                    assertEquals(room.getSquare(), 25.3);
                    assertEquals(room.getAdultBedCount(), 3);
                    assertEquals(room.getChildrenBedCount(), 0);
                    assertEquals(room.getCost(), BigDecimal.valueOf(1500.99));
                    assertEquals(room.getFloor(), 1);
                }));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForFindAll")
    void findAll_shouldFindAllRoomsByFilter(RoomFilter filter, Long expectedNumberOfRooms) {
        var pageable = PageRequest.of(0, 20);
        var actualRooms = roomService.findAll(filter, pageable);

        assertEquals(actualRooms.getTotalElements(), expectedNumberOfRooms);
    }

    static Stream<Arguments> getArgumentsForFindAll() {
        return Stream.of(
//                no-predicate filter
                Arguments.of(RoomFilter.builder().build(),
                        NUMBER_OF_ROOMS_WITH_NO_PREDICATE),
//                cost from-to filter
                Arguments.of(RoomFilter.builder()
                                .costFrom(BigDecimal.valueOf(1200))
                                .costTo(BigDecimal.valueOf(1900))
                                .build(),
                        NUMBER_OF_ROOMS_WITH_COST_FILTER)
        );
    }

    @Test
    void findAll_shouldFindAllRooms_whenNoConditionalFilterAndSquareDesSort() {
        var noConditionFilter = RoomFilter.builder().build();
        var sort = Sort.sort(Room.class).by(Room::getSquare).descending();
        var pageable = PageRequest.of(0, 20, sort);
        Double[] expectedSquares = {55.5, 45.0, 45.0, 45.0, 35.5, 35.5, 35.5, 35.5,
                25.5, 25.5, 25.3, 23.5, 20.5, 20.5, 20.5, 15.0};

        var actualRooms = roomService.findAll(noConditionFilter, pageable);
        var actualSquares = actualRooms.stream()
                .map(RoomReadDto::getSquare).toList();

        assertThat(actualSquares).containsExactly(expectedSquares);
    }
}