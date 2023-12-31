package com.sspdev.hotelbooking.unit.service;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.HotelContent;
import com.sspdev.hotelbooking.database.entity.HotelDetails;
import com.sspdev.hotelbooking.database.entity.QHotel;
import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Star;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.database.querydsl.QPredicates;
import com.sspdev.hotelbooking.database.repository.HotelRepository;
import com.sspdev.hotelbooking.dto.HotelContentCreateDto;
import com.sspdev.hotelbooking.dto.HotelContentReadDto;
import com.sspdev.hotelbooking.dto.HotelCreateEditDto;
import com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto;
import com.sspdev.hotelbooking.dto.HotelDetailsReadDto;
import com.sspdev.hotelbooking.dto.HotelReadDto;
import com.sspdev.hotelbooking.dto.filter.HotelFilter;
import com.sspdev.hotelbooking.mapper.HotelCreateEditMapper;
import com.sspdev.hotelbooking.mapper.HotelReadMapper;
import com.sspdev.hotelbooking.service.HotelContentService;
import com.sspdev.hotelbooking.service.HotelDetailsService;
import com.sspdev.hotelbooking.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
public class HotelServiceTest {

    private static final Integer FIRST_OWNER_ID = 4;
    private static final Integer SECOND_OWNER_ID = 5;
    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer NON_EXISTENT_HOTEL_ID = 99;
    private static final Integer EXISTENT_HOTEL_DETAILS_ID = 1;
    private static final Integer EXISTENT_HOTEL_CONTENT_ID = 1;

    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private HotelReadMapper hotelReadMapper;
    @Mock
    private HotelCreateEditMapper hotelCreateEditMapper;
    @Mock
    private HotelDetailsService hotelDetailsService;
    @Mock
    private HotelContentService hotelContentService;
    @InjectMocks
    private HotelService hotelService;

    @ParameterizedTest
    @MethodSource("getArgumentsForFindAll")
    void findAll_shouldFindAll(HotelFilter filter, List<Hotel> expectedHotels) {
        when(hotelRepository.findAllByFilter(filter)).thenReturn(expectedHotels);

        var actualHotels = hotelService.findAll(filter);

        assertEquals(expectedHotels.size(), actualHotels.size());
        verify(hotelReadMapper, times(expectedHotels.size())).map(any(Hotel.class));
    }

    static Stream<Arguments> getArgumentsForFindAll() {
        return Stream.of(
//                country = Russia filter -> 3 hotels
                Arguments.of(HotelFilter.builder()
                                .country("Russia")
                                .build(),
                        List.of(new Hotel(), new Hotel(), new Hotel())),
//                with "plaza" in hotel name filter -> 4 hotels
                Arguments.of(HotelFilter.builder()
                                .name("plaza")
                                .build(),
                        List.of(new Hotel(), new Hotel(), new Hotel()), new Hotel()));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForFindAllByOwnerId")
    void findAllByOwnerId_whenHotelsExist(Integer ownerId, List<Hotel> expectedHotels) {
        doReturn(expectedHotels).when(hotelRepository).findAllByOwnerId(ownerId);

        var actualHotels = hotelService.findAllByOwnerId(ownerId);

        assertEquals(actualHotels.size(), expectedHotels.size());
        verify(hotelReadMapper, times(expectedHotels.size())).map(any(Hotel.class));
    }

    static Stream<Arguments> getArgumentsForFindAllByOwnerId() {
        return Stream.of(
                Arguments.of(FIRST_OWNER_ID,
                        List.of(new Hotel(), new Hotel(), new Hotel())),
                Arguments.of(SECOND_OWNER_ID,
                        List.of(new Hotel(), new Hotel()))
        );
    }

    @Test
    void findAll_shouldFindAllHotels_whenHotelsExist() {
        var hotels = List.of(getHotel(), getHotel());
        var predicate = QPredicates.builder().build();
        var pageable = PageRequest.of(0, 20);
        var filter = HotelFilter.builder().build();
        Page<Hotel> hotelPage = new PageImpl<>(hotels);

        when(hotelRepository.findAll(predicate, pageable)).thenReturn(hotelPage);
        var actualHotels = hotelService.findAllByFilter(filter, pageable);

        assertEquals(actualHotels.getTotalElements(), 2L);
        verify(hotelReadMapper, times(2)).map(any(Hotel.class));
    }

    @Test
    void findAllByFilter_shouldReturnAllHotels() {
        HotelFilter filter = HotelFilter.builder().build();
        var predicate = QPredicates.builder().add(filter.name(), QHotel.hotel.name::eq).build();
        var pageable = PageRequest.of(0, 20);
        Page<Hotel> hotelPage = new PageImpl<>(List.of(getHotel(), getHotel()));
        when(hotelRepository.findAll(predicate, pageable)).thenReturn(hotelPage);

        var actualHotels = hotelService.findAllByFilter(filter, pageable);

        assertEquals(actualHotels.getTotalElements(), 2L);
        verify(hotelReadMapper, times(2)).map(any(Hotel.class));
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

    @Test
    void shouldCreateNewHotel() {
        var hotel = getHotel();
        var hotelCreateEditDto = getHotelCreateEditDto();
        var hotelContentCreateDto = getHotelContentCreateDto();
        var hotelDetailsCreatedEditDto = getHotelDetailsCreatedEditDto();
        var hotelReadDto = getHotelReadDto();
        when(hotelCreateEditMapper.map(hotelCreateEditDto)).thenReturn(hotel);
        when(hotelRepository.save(hotel)).thenReturn(hotel);
        when(hotelReadMapper.map(hotel)).thenReturn(hotelReadDto);

        var actualResult = hotelService.create(hotelCreateEditDto, hotelDetailsCreatedEditDto, hotelContentCreateDto);

        assertThat(actualResult.getId()).isPositive();
        verify(hotelDetailsService).create(hotelDetailsCreatedEditDto);
    }

    @Test()
    void shouldUpdateExistentHotel() {
        var hotel = getHotel();
        var hotelCreateEditDto = getHotelCreateEditDto();
        var hotelDetailsCreatedEditDto = getHotelDetailsCreatedEditDto();
        var hotelReadDto = getHotelReadDto();
        when(hotelRepository.findById(EXISTENT_HOTEL_ID)).thenReturn(Optional.of(hotel));
        when(hotelCreateEditMapper.map(hotelCreateEditDto, hotel)).thenReturn(hotel);
        when(hotelRepository.saveAndFlush(hotel)).thenReturn(hotel);
        when(hotelReadMapper.map(hotel)).thenReturn(hotelReadDto);

        var update = hotelService.update(EXISTENT_HOTEL_DETAILS_ID, hotelCreateEditDto, hotelDetailsCreatedEditDto);

        assertThat(update).isPresent();
        verify(hotelDetailsService).update(hotel.getId(), hotelDetailsCreatedEditDto);
    }

    @Test
    void delete_shouldDeleteHotelWithHotelDetailsAndContent_whenEverythingExist() {
        var hotel = getHotel();
        when(hotelRepository.findById(EXISTENT_HOTEL_ID)).thenReturn(Optional.of(hotel));

        var actualResult = hotelService.delete(EXISTENT_HOTEL_ID);

        verify(hotelContentService).deleteAllByHotel(hotel.getId());
        verify(hotelDetailsService).delete(hotel.getId());
        verify(hotelRepository).delete(hotel);
        verify(hotelRepository).flush();
        assertTrue(actualResult);
    }

    private Hotel getHotel() {
        return Hotel.builder()
                .id(EXISTENT_HOTEL_ID)
                .owner(getUser())
                .name("MoscowPlaza")
                .available(true)
                .status(Status.APPROVED)
                .build();
    }

    private User getUser() {
        return User.builder()
                .id(FIRST_OWNER_ID)
                .role(Role.ADMIN)
                .build();
    }

    private HotelReadDto getHotelReadDto() {
        return new HotelReadDto(
                EXISTENT_HOTEL_ID,
                FIRST_OWNER_ID,
                "MoscowPlaza",
                true,
                Status.APPROVED);
    }

    private HotelCreateEditDto getHotelCreateEditDto() {
        return new HotelCreateEditDto(
                FIRST_OWNER_ID,
                "MoscowPlaza",
                true,
                Status.APPROVED
        );
    }

    private HotelDetailsCreateEditDto getHotelDetailsCreatedEditDto() {
        return new HotelDetailsCreateEditDto(
                EXISTENT_HOTEL_ID,
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

    private HotelContentReadDto getHotelContentReadDto() {
        return new HotelContentReadDto(
                EXISTENT_HOTEL_CONTENT_ID,
                "test.jpg",
                ContentType.PHOTO.name(),
                EXISTENT_HOTEL_ID
        );
    }

    private HotelContent getHotelContent() {
        return new HotelContent(
                EXISTENT_HOTEL_CONTENT_ID,
                getHotel(),
                "test.jpg",
                ContentType.PHOTO
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

    private HotelContentCreateDto getHotelContentCreateDto() {
        var content = new MockMultipartFile("test.jgp", "test.jpg".getBytes());
        return new HotelContentCreateDto(
                content,
                ContentType.PHOTO,
                EXISTENT_HOTEL_ID
        );
    }
}