package com.sspdev.hotelbooking.service;

import com.sspdev.hotelbooking.database.repository.HotelRepository;
import com.sspdev.hotelbooking.dto.HotelReadDto;
import com.sspdev.hotelbooking.mapper.HotelReadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelReadMapper hotelReadMapper;

    public List<HotelReadDto> findAllByOwnerId(Integer ownerId) {
        return hotelRepository.findAllByOwnerId(ownerId).stream()
                .map(hotelReadMapper::map)
                .toList();
    }

    public List<HotelReadDto> findAll() {
        return hotelRepository.findAll().stream()
                .map(hotelReadMapper::map)
                .toList();
    }

}