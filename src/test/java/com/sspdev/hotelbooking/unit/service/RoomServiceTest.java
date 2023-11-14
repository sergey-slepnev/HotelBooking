package com.sspdev.hotelbooking.unit.service;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.database.querydsl.QPredicates;
import com.sspdev.hotelbooking.database.repository.RoomRepository;
import com.sspdev.hotelbooking.dto.RoomContentCreateDto;
import com.sspdev.hotelbooking.dto.RoomContentReadDto;
import com.sspdev.hotelbooking.dto.RoomCreateEditDto;
import com.sspdev.hotelbooking.dto.RoomReadDto;
import com.sspdev.hotelbooking.dto.filter.RoomFilter;
import com.sspdev.hotelbooking.mapper.RoomCreateEditMapper;
import com.sspdev.hotelbooking.mapper.RoomReadMapper;
import com.sspdev.hotelbooking.service.RoomContentService;
import com.sspdev.hotelbooking.service.RoomService;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
public class RoomServiceTest extends UnitTestBase {

    private static final Integer EXISTENT_ROOM_ID = 1;
    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer EXISTENT_ROOM_CONTENT_ID = 1;
    private static final Integer NOT_EXISTENT_ROOM_ID = 999;

    @MockBean
    private final RoomReadMapper roomReadMapper;
    @MockBean
    private final RoomRepository roomRepository;
    @MockBean
    private final RoomContentService roomContentService;
    @MockBean
    private final RoomCreateEditMapper roomCreateEditMapper;
    @InjectMocks
    private final RoomService roomService;

    @Test
    void findById_shouldFindHotelById_whenHotelsExist() {
        var room = getRoom();
        when(roomRepository.findById(EXISTENT_ROOM_ID)).thenReturn(Optional.of(room));
        var roomReadDto = getRoomReadDto();
        when(roomReadMapper.map(room)).thenReturn(roomReadDto);

        var actualRoom = roomService.findById(EXISTENT_ROOM_ID);
        assertThat(actualRoom).isPresent();
    }

    @Test
    void findAll_ShouldFindAllRoomsByFilter() {
        var roomFilter = RoomFilter.builder().build();
        var predicate = QPredicates.builder().build();
        var pageable = PageRequest.of(0, 20);
        Page<Room> roomPage = new PageImpl<>(List.of(getRoom(), getRoom(), getRoom()));
        when(roomRepository.findAll(predicate, pageable)).thenReturn(roomPage);

        var actualRooms = roomService.findAll(roomFilter, pageable);

        assertEquals(actualRooms.getTotalElements(), 3L);
        verify(roomReadMapper, times(3)).map(any(Room.class));
    }

    @Test
    void create_shouldCreateNewRoom() {
        var roomCreateEditDto = getRoomCreateEditDto();
        var room = getRoom();
        var roomReadDto = getRoomReadDto();
        var roomContentCreateDto = getRoomContentCreateDto();
        var roomContentReadDto = getRoomContentReadDto();
        when(roomCreateEditMapper.map(roomCreateEditDto)).thenReturn(room);
        when(roomRepository.save(room)).thenReturn(room);
        when(roomContentService.save(roomContentCreateDto)).thenReturn(roomContentReadDto);
        when(roomReadMapper.map(room)).thenReturn(roomReadDto);

        var actualRoom = roomService.create(roomCreateEditDto, roomContentCreateDto);

        assertThat(actualRoom).isNotNull();
    }

    @Test
    void update_shouldUpdateExistentRoom() {
        var room = getRoom();
        var roomCreateEditDto = getRoomCreateEditDto();
        var roomReadDto = getRoomReadDto();
        when(roomRepository.findById(EXISTENT_ROOM_ID)).thenReturn(Optional.of(room));
        when(roomCreateEditMapper.map(roomCreateEditDto, room)).thenReturn(room);
        when(roomRepository.saveAndFlush(room)).thenReturn(room);
        when(roomReadMapper.map(room)).thenReturn(roomReadDto);

        var actualRoom = roomService.update(room.getId(), roomCreateEditDto);

        assertThat(actualRoom).isPresent();
    }

    @Test
    void delete_shouldReturnTrue_whenRoomExists() {
        var existentRoom = getRoom();
        when(roomRepository.findById(EXISTENT_ROOM_ID)).thenReturn(Optional.of(existentRoom));

        var isDeleted = roomService.delete(EXISTENT_ROOM_ID);

        assertTrue(isDeleted);
        verify(roomRepository, times(1)).delete(existentRoom);
        verify(roomRepository, times(1)).flush();
    }

    @Test
    void delete_shouldReturnFalse_whenRoomNotExist() {
        when(roomRepository.findById(NOT_EXISTENT_ROOM_ID)).thenReturn(Optional.empty());
        var isDeleted = roomService.delete(NOT_EXISTENT_ROOM_ID);

        assertFalse(isDeleted);
    }

    @Test
    void findByHotel_shouldFindRoomsByHotel_whenRoomsExist() {
        var existentRoom = getRoom();
        when(roomRepository.findByHotelId(EXISTENT_HOTEL_ID)).thenReturn(List.of(existentRoom, existentRoom));

        var expectedRooms = roomService.findByHotel(EXISTENT_HOTEL_ID);

        assertThat(expectedRooms).hasSize(2);
        verify(roomReadMapper, times(2)).map(existentRoom);
    }

    @Test
    void findByHotel_shouldReturnEmptyListOfRooms() {
        var existentRoom = getRoom();
        when(roomRepository.findByHotelId(EXISTENT_HOTEL_ID)).thenReturn(Collections.emptyList());

        var expectedRooms = roomService.findByHotel(EXISTENT_HOTEL_ID);

        assertThat(expectedRooms).hasSize(0);
        verifyNoInteractions(roomReadMapper);
    }

    private Room getRoom() {
        return Room.builder()
                .id(EXISTENT_ROOM_ID)
                .hotel(getHotel())
                .roomNo(1)
                .type(RoomType.DBL)
                .square(44.4)
                .adultBedCount(3)
                .childrenBedCount(0)
                .cost(BigDecimal.valueOf(1900))
                .floor(1)
                .available(true)
                .description("Отличный отель")
                .roomContents(null)
                .build();
    }

    private Hotel getHotel() {
        return Hotel.builder()
                .id(EXISTENT_HOTEL_ID)
                .owner(null)
                .name("MoscowPlaza")
                .available(true)
                .status(Status.APPROVED)
                .build();
    }

    private RoomReadDto getRoomReadDto() {
        return new RoomReadDto(
                EXISTENT_ROOM_ID,
                EXISTENT_HOTEL_ID,
                1,
                RoomType.DBL,
                44.4,
                3,
                0,
                BigDecimal.valueOf(1900),
                1,
                true,
                "Отличный отель",
                null
        );
    }

    private RoomCreateEditDto getRoomCreateEditDto() {
        return new RoomCreateEditDto(
                EXISTENT_HOTEL_ID,
                1,
                RoomType.DBL,
                44.4,
                3,
                0,
                BigDecimal.valueOf(1900),
                1,
                true,
                "Отличный отель",
                null
        );
    }

    private RoomContentCreateDto getRoomContentCreateDto() {
        return new RoomContentCreateDto(
                new MockMultipartFile("RoomPhoto.jpg", "RoomPhoto.jpg", "application/octet-stream", new byte[]{}),
                ContentType.PHOTO,
                EXISTENT_ROOM_ID
        );
    }

    private RoomContentReadDto getRoomContentReadDto() {
        return new RoomContentReadDto(
                EXISTENT_ROOM_CONTENT_ID,
                "RoomPhoto.jpg",
                ContentType.PHOTO.name(),
                EXISTENT_ROOM_ID
        );
    }
}