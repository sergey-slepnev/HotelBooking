package com.sspdev.hotelbooking.unit.service;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.repository.HotelRepository;
import com.sspdev.hotelbooking.mapper.HotelReadMapper;
import com.sspdev.hotelbooking.service.HotelService;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RequiredArgsConstructor
public class HotelServiceTest extends UnitTestBase {

    private static final Integer FIRST_OWNER_ID = 4;
    private static final Integer SECOND_OWNER_ID = 5;

    @MockBean
    private final HotelRepository hotelRepository;

    @MockBean
    private final HotelReadMapper hotelReadMapper;

    @InjectMocks
    private final HotelService hotelService;

    @ParameterizedTest
    @MethodSource("getArgumentsForFindAllByOwnerId")
    void checkFindAllByOwnerId(Integer ownerId, List<Hotel> expectedHotels) {
        doReturn(expectedHotels).when(hotelRepository).findAllByOwnerId(ownerId);

        var actualHotels = hotelService.findAllByOwnerId(ownerId);

        assertEquals(actualHotels.size(), expectedHotels.size());
        verify(hotelReadMapper, times(expectedHotels.size())).map(any(Hotel.class));
    }

    public static Stream<Arguments> getArgumentsForFindAllByOwnerId() {
        return Stream.of(
                Arguments.of(FIRST_OWNER_ID,
                        List.of(new Hotel(), new Hotel(), new Hotel())),
                Arguments.of(SECOND_OWNER_ID,
                        List.of(new Hotel(), new Hotel()))
        );
    }

    @Test
    void shouldFindAllHotels() {
        var allHotels = List.of(new Hotel(), new Hotel(), new Hotel(), new Hotel(), new Hotel());
        doReturn(allHotels).when(hotelRepository).findAll();

        var actualHotels = hotelService.findAll();

        assertThat(actualHotels).hasSize(5);
        verify(hotelReadMapper, times(actualHotels.size())).map(any(Hotel.class));
    }
}