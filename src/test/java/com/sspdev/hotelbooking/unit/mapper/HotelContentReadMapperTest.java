package com.sspdev.hotelbooking.unit.mapper;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.HotelContent;
import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.dto.HotelContentReadDto;
import com.sspdev.hotelbooking.mapper.HotelContentReadMapper;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
class HotelContentReadMapperTest extends UnitTestBase {

    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer EXISTENT_HOTEL_CONTENT_ID = 1;

    private final HotelContentReadMapper hotelContentReadMapper;

    @Test
    void map_shouldMapHotelContentToHotelContentReadDto() {
        var hotelContent = getHotelContent();
        var hotelContentReadDto = getHotelContentReadDto();

        var expectedContentReadDto = hotelContentReadMapper.map(hotelContent);

        assertEquals(hotelContentReadDto, expectedContentReadDto);
    }

    private HotelContent getHotelContent() {
        return new HotelContent(
                EXISTENT_HOTEL_CONTENT_ID,
                getHotel(),
                "test.jpg",
                ContentType.PHOTO
        );
    }

    private Hotel getHotel() {
        return Hotel.builder()
                .id(EXISTENT_HOTEL_ID)
                .build();
    }

    private HotelContentReadDto getHotelContentReadDto() {
        return new HotelContentReadDto(
                EXISTENT_HOTEL_CONTENT_ID,
                "test.jpg",
                ContentType.PHOTO.name(),
                EXISTENT_HOTEL_ID
        );

    }
}