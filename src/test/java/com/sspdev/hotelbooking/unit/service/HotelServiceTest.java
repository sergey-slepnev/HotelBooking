package com.sspdev.hotelbooking.unit.service;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.database.repository.HotelRepository;
import com.sspdev.hotelbooking.dto.HotelReadDto;
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
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
public class HotelServiceTest extends UnitTestBase {

    private static final Integer FIRST_OWNER_ID = 4;
    private static final Integer SECOND_OWNER_ID = 5;
    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer NON_EXISTENT_HOTEL_ID = 99;

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

    @Test
    void shouldFindExistentHotelById() {
        when(hotelRepository.findById(EXISTENT_HOTEL_ID)).thenReturn(Optional.of(getHotel()));
        when(hotelReadMapper.map(getHotel())).thenReturn(getHotelReadDto());

        var actualHotel = hotelService.findById(EXISTENT_HOTEL_ID);
        assertThat(actualHotel).isPresent();
    }

    @Test
    void shouldReturnEmptyIfHotelNonExistent() {
        when(hotelRepository.findById(NON_EXISTENT_HOTEL_ID)).thenReturn(Optional.empty());

        var actualResult = hotelService.findById(NON_EXISTENT_HOTEL_ID);

        assertThat(actualResult).isEmpty();
        verifyNoInteractions(hotelReadMapper);
    }

    private static Hotel getHotel() {
        return Hotel.builder()
                .id(EXISTENT_HOTEL_ID)
                .owner(getUser())
                .name("MoscowPlaza")
                .available(true)
                .status(Status.APPROVED)
                .build();
    }

    private static User getUser() {
        return User.builder()
                .id(FIRST_OWNER_ID)
                .role(Role.ADMIN)
                .build();
    }

    private static HotelReadDto getHotelReadDto() {
        return new HotelReadDto(
                EXISTENT_HOTEL_ID,
                FIRST_OWNER_ID,
                "MoscowPlaza",
                true,
                Status.APPROVED);
    }
}