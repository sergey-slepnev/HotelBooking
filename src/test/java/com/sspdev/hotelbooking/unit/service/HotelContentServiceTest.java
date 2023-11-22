package com.sspdev.hotelbooking.unit.service;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.HotelContent;
import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.database.repository.HotelContentRepository;
import com.sspdev.hotelbooking.dto.HotelContentReadDto;
import com.sspdev.hotelbooking.mapper.HotelContentReadMapper;
import com.sspdev.hotelbooking.service.ApplicationContentService;
import com.sspdev.hotelbooking.service.HotelContentService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
public class HotelContentServiceTest {

    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer NON_EXISTENT_HOTEL_ID = 999;
    private static final Integer EXISTENT_HOTEL_CONTENT_ID = 1;

    @Mock
    private HotelContentRepository hotelContentRepository;
    @Mock
    private HotelContentReadMapper hotelContentReadMapper;
    @Mock
    private ApplicationContentService applicationContentService;
    @InjectMocks
    private HotelContentService hotelContentService;

    @Test
    void findContent_shouldFindContentByHotelId() {
        var hotelContent = getHotelContent();
        var hotelContentReadDto = getHotelContentReadDto();
        when(hotelContentRepository.findByHotelId(EXISTENT_HOTEL_ID)).thenReturn(List.of(hotelContent));
        when(hotelContentReadMapper.map(hotelContent)).thenReturn(hotelContentReadDto);

        var expectedContent = hotelContentService.findContent(EXISTENT_HOTEL_ID);
        var links = expectedContent.stream().map(HotelContentReadDto::getLink).toList();

        assertThat(links).hasSize(1);
        assertThat(links).contains("test.jpg");
    }

    @Test
    void findContent_shouldReturnZero() {
        when(hotelContentRepository.findByHotelId(NON_EXISTENT_HOTEL_ID)).thenReturn(emptyList());

        var expectedContent = hotelContentService.findContent(NON_EXISTENT_HOTEL_ID);
        var links = expectedContent.stream().map(HotelContentReadDto::getLink).toList();

        assertThat(links).isEmpty();
        verifyNoInteractions(hotelContentReadMapper);
    }

    @Test
    void deleteAllByHotel_shouldDeleteAllContentsForHotel() {
        var existentContents = List.of(getHotelContent(), getHotelContent());
        when(hotelContentRepository.findByHotelId(EXISTENT_HOTEL_ID)).thenReturn(existentContents);
        when(hotelContentReadMapper.map(existentContents.get(0))).thenReturn(getHotelContentReadDto());
        when(hotelContentReadMapper.map(existentContents.get(1))).thenReturn(getHotelContentReadDto());
        var existentContent = hotelContentService.findContent(EXISTENT_HOTEL_ID);
        assertThat(existentContent).hasSize(2);
        existentContent.forEach(System.out::println);

        doNothing().when(hotelContentRepository).deleteAll(existentContents);
        doNothing().when(hotelContentRepository).flush();
        doNothing().when(applicationContentService).deleteContentFromStore(anyString());
        hotelContentService.deleteAllByHotel(EXISTENT_HOTEL_ID);
        reset(hotelContentRepository);

        var actualContent = hotelContentService.findContent(EXISTENT_HOTEL_ID);
        assertThat(actualContent).hasSize(0);
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