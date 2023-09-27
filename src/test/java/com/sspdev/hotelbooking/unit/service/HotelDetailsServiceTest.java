package com.sspdev.hotelbooking.unit.service;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.HotelDetails;
import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Star;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.database.repository.HotelDetailsRepository;
import com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto;
import com.sspdev.hotelbooking.dto.HotelDetailsReadDto;
import com.sspdev.hotelbooking.mapper.HotelDetailsCreateEditMapper;
import com.sspdev.hotelbooking.mapper.HotelDetailsReadMapper;
import com.sspdev.hotelbooking.service.HotelDetailsService;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
public class HotelDetailsServiceTest extends UnitTestBase {

    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer EXISTENT_HOTEL_DETAILS_ID = 1;
    private static final Integer FIRST_OWNER_ID = 1;

    @MockBean
    private final HotelDetailsReadMapper hotelDetailsReadMapper;

    @MockBean
    private final HotelDetailsCreateEditMapper hotelDetailsCreateEditMapper;

    @MockBean
    private final HotelDetailsRepository hotelDetailsRepository;

    @InjectMocks
    private final HotelDetailsService hotelDetailsService;

    @Test
    void findByHotelId_shouldFindHotelDetailsByHotelId_whenHotelExistent() {
        var hotelDetails = getHotelDetails();
        var hotelDetailsReadDto = getHotelDetailsReadDto();
        when(hotelDetailsRepository.findByHotelId(EXISTENT_HOTEL_DETAILS_ID)).thenReturn(Optional.of(hotelDetails));
        when(hotelDetailsReadMapper.map(hotelDetails)).thenReturn(hotelDetailsReadDto);

        var actualHotelDetails = hotelDetailsService.findByHotelId(EXISTENT_HOTEL_ID);

        assertThat(actualHotelDetails).isPresent();
    }

    @Test
    void checkCreate() {
        var hotelDetailsCreatedEditDto = getHotelDetailsCreatedEditDto();
        var hotelDetails = getHotelDetails();
        var hotelDetailsReadDto = getHotelDetailsReadDto();
        when(hotelDetailsCreateEditMapper.map(hotelDetailsCreatedEditDto)).thenReturn(hotelDetails);
        when(hotelDetailsRepository.save(hotelDetails)).thenReturn(hotelDetails);
        when(hotelDetailsReadMapper.map(hotelDetails)).thenReturn(hotelDetailsReadDto);

        var actualHotelDetails = hotelDetailsService.create(hotelDetailsCreatedEditDto);

        assertThat(actualHotelDetails).isNotNull();
    }

    @Test
    void checkUpdate() {
        var hotelDetailsCreatedEditDto = getHotelDetailsCreatedEditDto();
        var hotelDetails = getHotelDetails();
        var hotelDetailsReadDto = getHotelDetailsReadDto();
        when(hotelDetailsRepository.findById(EXISTENT_HOTEL_DETAILS_ID)).thenReturn(Optional.of(hotelDetails));
        when(hotelDetailsCreateEditMapper.map(hotelDetailsCreatedEditDto, hotelDetails)).thenReturn(hotelDetails);
        when(hotelDetailsRepository.saveAndFlush(hotelDetails)).thenReturn(hotelDetails);
        when(hotelDetailsReadMapper.map(hotelDetails)).thenReturn(hotelDetailsReadDto);

        var actualHotelDetails = hotelDetailsService.update(hotelDetails.getId(), hotelDetailsCreatedEditDto);

        assertThat(actualHotelDetails).isPresent();
    }

    private HotelDetailsReadDto getHotelDetailsReadDto() {
        return new HotelDetailsReadDto(
                EXISTENT_HOTEL_DETAILS_ID,
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

    private HotelDetails getHotelDetails() {
        return new HotelDetails(
                EXISTENT_HOTEL_DETAILS_ID,
                getHotel(),
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
}