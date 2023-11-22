package com.sspdev.hotelbooking.unit.http.rest;

import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.dto.HotelContentReadDto;
import com.sspdev.hotelbooking.http.rest.HotelContentRestController;
import com.sspdev.hotelbooking.service.ApplicationContentService;
import com.sspdev.hotelbooking.service.HotelContentService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class HotelContentRestControllerTest {

    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer EXISTENT_CONTENT_ID = 1;
    private static final Integer NON_EXISTENT_CONTENT_ID = 999;
    private static final Integer EXISTENT_HOTEL_CONTENT_ID = 1;

    private MockMvc mockMvc;
    @Mock
    private HotelContentService hotelContentService;
    @Mock
    private ApplicationContentService applicationContentService;
    @InjectMocks
    private HotelContentRestController hotelContentRestController;

    @BeforeEach
    public void initMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(hotelContentRestController).build();
    }

    @Test
    void findContent_shouldFindHotelContent_whenContentExists() throws Exception {
        var hotelContent = getHotelContentReadDto();
        when(hotelContentService.findContent(EXISTENT_HOTEL_ID)).thenReturn(List.of(hotelContent));
        when(applicationContentService.getImage(hotelContent.getLink())).thenReturn(Optional.of("test.jpg".getBytes()));

        mockMvc.perform(get("/api/v1/hotels/" + EXISTENT_HOTEL_ID + "/content/" + EXISTENT_CONTENT_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }

    @Test
    void findContent_shouldReturnNotFound_whenContentNotExists() throws Exception {
        when(hotelContentService.findContent(EXISTENT_HOTEL_ID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/hotels/" + EXISTENT_HOTEL_ID + "/content/" + NON_EXISTENT_CONTENT_ID))
                .andDo(print())
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