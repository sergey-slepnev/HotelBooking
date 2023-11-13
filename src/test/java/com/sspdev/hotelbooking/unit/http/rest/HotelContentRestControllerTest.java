package com.sspdev.hotelbooking.unit.http.rest;

import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.dto.HotelContentReadDto;
import com.sspdev.hotelbooking.service.ApplicationContentService;
import com.sspdev.hotelbooking.service.HotelContentService;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@AutoConfigureMockMvc
@WithMockUser(username = "test@gmail.com", password = "test", authorities = {"ADMIN", "USER", "OWNER"})
class HotelContentRestControllerTest extends UnitTestBase {

    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer EXISTENT_CONTENT_ID = 1;
    private static final Integer NON_EXISTENT_CONTENT_ID = 999;
    private static final Integer EXISTENT_HOTEL_CONTENT_ID = 1;

    private final MockMvc mockMvc;

    @MockBean
    private final HotelContentService hotelContentService;

    @MockBean
    private final ApplicationContentService applicationContentService;

    @Test
    void findContent_shouldFindHotelContent_whenContentExists() throws Exception {
        var hotelContent = getHotelContentReadDto();
        when(hotelContentService.findContent(EXISTENT_HOTEL_ID)).thenReturn(List.of(hotelContent));
        when(applicationContentService.getImage(hotelContent.getLink())).thenReturn(Optional.of("test.jpg".getBytes()));

        mockMvc.perform(get("/api/v1/hotels/" + EXISTENT_HOTEL_ID + "/content/" + EXISTENT_CONTENT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }

    @Test
    void findContent_shouldReturnNotFound_whenContentNotExists() throws Exception {
        when(hotelContentService.findContent(NON_EXISTENT_CONTENT_ID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/hotels/" + EXISTENT_HOTEL_ID + "/content/" + NON_EXISTENT_CONTENT_ID))
                .andExpect(status().isNotFound());
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