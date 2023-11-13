package com.sspdev.hotelbooking.integration.service;

import com.sspdev.hotelbooking.dto.HotelContentReadDto;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import com.sspdev.hotelbooking.service.HotelContentService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
class HotelContentServiceIT extends IntegrationTestBase {

    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer NON_EXISTENT_HOTEL_ID = 999;

    private final HotelContentService hotelContentService;

    @Test
    void findContent_shouldFindContentByHotelId() {
        var expectedContent = hotelContentService.findContent(EXISTENT_HOTEL_ID);
        var links = expectedContent.stream().map(HotelContentReadDto::getLink).toList();

        assertThat(links).hasSize(2);
        assertThat(links).contains("moscowPlaza.jpg", "moscowPlaza.MP4");
    }

    @Test
    void findContent_shouldReturnZero() {
        var expectedContent = hotelContentService.findContent(NON_EXISTENT_HOTEL_ID);
        var links = expectedContent.stream().map(HotelContentReadDto::getLink).toList();

        assertThat(links).isEmpty();
    }
}