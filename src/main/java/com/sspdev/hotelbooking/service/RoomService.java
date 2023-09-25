package com.sspdev.hotelbooking.service;

import com.sspdev.hotelbooking.database.repository.RoomRepository;
import com.sspdev.hotelbooking.dto.RoomReadDto;
import com.sspdev.hotelbooking.dto.filter.RoomFilter;
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

    public Optional<RoomReadDto> findById(Integer roomId) {
        return roomRepository.findById(roomId)
                .map(roomReadMapper::map);
    }

    public Page<RoomReadDto> findAll(RoomFilter filter, Pageable pageable) {
        var predicate = com.sspdev.hotelrepository.database.querydsl.QPredicates.builder()
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
}
