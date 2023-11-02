package com.sspdev.hotelbooking.service;

import com.sspdev.hotelbooking.database.querydsl.QPredicates;
import com.sspdev.hotelbooking.database.repository.RoomRepository;
import com.sspdev.hotelbooking.dto.RoomContentCreateDto;
import com.sspdev.hotelbooking.dto.RoomCreateEditDto;
import com.sspdev.hotelbooking.dto.RoomReadDto;
import com.sspdev.hotelbooking.dto.filter.RoomFilter;
import com.sspdev.hotelbooking.mapper.RoomContentCreateMapper;
import com.sspdev.hotelbooking.mapper.RoomCreateEditMapper;
import com.sspdev.hotelbooking.mapper.RoomReadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.sspdev.hotelbooking.database.entity.QHotel.hotel;
import static com.sspdev.hotelbooking.database.entity.QRoom.room;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomReadMapper roomReadMapper;
    private final RoomCreateEditMapper roomCreateEditMapper;
    private final RoomContentService roomContentService;
    private final RoomContentCreateMapper roomContentCreateMapper;

    public Optional<RoomReadDto> findById(Integer roomId) {
        return roomRepository.findById(roomId)
                .map(roomReadMapper::map);
    }

    public Page<RoomReadDto> findAll(RoomFilter filter, Pageable pageable) {
        var predicate = QPredicates.builder()
                .add(filter.country(), hotel.hotelDetails.country::eq)
                .add(filter.locality(), hotel.hotelDetails.locality::eq)
                .add(filter.star(), hotel.hotelDetails.star::eq)
                .add(filter.costFrom(), room.cost::goe)
                .add(filter.costTo(), room.cost::loe)
                .add(filter.adultBedCount(), room.adultBedCount::eq)
                .add(filter.childrenBedCount(), room.childrenBedCount::eq)
                .build();

        return roomRepository.findAll(predicate, pageable)
                .map(roomReadMapper::map);
    }

    @Transactional
    public RoomReadDto create(RoomCreateEditDto roomDto, RoomContentCreateDto contentCreateDto) {
        return Optional.of(roomDto)
                .map(roomCreateEditMapper::map)
                .map(roomRepository::save)
                .map(roomReadMapper::map)
                .map(room -> {
                    contentCreateDto.setRoomId(room.getId());
                    roomContentService.save(contentCreateDto);
                    return room;
                })
                .orElseThrow();
    }

    @Transactional
    public Optional<RoomReadDto> update(Integer roomId, RoomCreateEditDto roomCreateEditDto) {
        return roomRepository.findById(roomId)
                .map(roomEntity -> roomCreateEditMapper.map(roomCreateEditDto, roomEntity))
                .map(roomRepository::saveAndFlush)
                .map(roomReadMapper::map);
    }
}