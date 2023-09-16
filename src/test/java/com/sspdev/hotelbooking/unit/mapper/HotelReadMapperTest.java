package com.sspdev.hotelbooking.unit.mapper;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.PersonalInfo;
import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.HotelReadDto;
import com.sspdev.hotelbooking.mapper.HotelReadMapper;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
class HotelReadMapperTest extends UnitTestBase {

    private final HotelReadMapper hotelReadMapper;

    @Test
    void shouldReturnUserReadDtoFromUser() {
        var hotelEntity = getHotel();
        var expectedHotelDto = getHotelReadDto();

        var actualHotelDto = hotelReadMapper.map(hotelEntity);

        assertEquals(expectedHotelDto, actualHotelDto);
    }

    private Hotel getHotel() {
        return Hotel.builder()
                .id(1)
                .owner(getUser())
                .name("My best hotel")
                .available(true)
                .status(Status.NEW)
                .build();
    }

    private User getUser() {
        return User.builder()
                .id(1)
                .role(Role.USER)
                .username("user")
                .password("123")
                .personalInfo(new PersonalInfo("Petr", "Petrov", LocalDate.of(2000, 10, 10)))
                .phone("+7-954-984-98-98")
                .image("user_avatar")
                .status(Status.NEW)
                .registeredAt(LocalDateTime.of(2023, 5, 5, 10, 10))
                .build();
    }

    private HotelReadDto getHotelReadDto() {
        return new HotelReadDto(
                1,
                1,
                "My best hotel",
                true,
                Status.NEW
        );
    }
}