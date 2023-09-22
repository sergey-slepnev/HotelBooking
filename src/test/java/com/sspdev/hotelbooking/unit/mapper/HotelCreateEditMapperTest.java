package com.sspdev.hotelbooking.unit.mapper;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.HotelCreateEditDto;
import com.sspdev.hotelbooking.mapper.HotelCreateEditMapper;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
class HotelCreateEditMapperTest extends UnitTestBase {

    private final HotelCreateEditMapper hotelCreateEditMapper;

    @Test
    void shouldMapFromHotelCreateEditDtoToNewHotel() {
        var hotelCreateEditDto = new HotelCreateEditDto(
                4,
                "PalmBeachHotel",
                null,
                null);

        var expectedHotel = Hotel.builder()
                .name("PalmBeachHotel")
                .available(false)
                .status(Status.NEW)
                .build();

        var actualHotel = hotelCreateEditMapper.map(hotelCreateEditDto);

        assertAll(() -> {
            assertEquals(expectedHotel.getId(), actualHotel.getId());
            assertEquals(expectedHotel.getName(), actualHotel.getName());
            assertEquals(expectedHotel.isAvailable(), actualHotel.isAvailable());
            assertEquals(expectedHotel.getStatus(), actualHotel.getStatus());
        });
    }

    @Test
    void shouldCopyFromCreateEditHotelDtoToExistentHotel() {
        var hotelCreateEditDto = new HotelCreateEditDto(
                4,
                "PalmBeachHotel",
                true,
                Status.APPROVED
        );
        var expectedHotel = Hotel.builder()
                .name("PalmBeachHotel")
                .available(true)
                .status(Status.APPROVED)
                .build();

        var actualHotel = hotelCreateEditMapper.map(hotelCreateEditDto, expectedHotel);

        assertAll(() -> {
            assertEquals(expectedHotel.getId(), actualHotel.getId());
            assertEquals(expectedHotel.getName(), actualHotel.getName());
            assertEquals(expectedHotel.isAvailable(), actualHotel.isAvailable());
            assertEquals(expectedHotel.getStatus(), actualHotel.getStatus());
        });
    }
}