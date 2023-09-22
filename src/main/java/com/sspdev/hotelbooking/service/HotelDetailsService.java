package com.sspdev.hotelbooking.service;

import com.sspdev.hotelbooking.database.repository.HotelDetailsRepository;
import com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto;
import com.sspdev.hotelbooking.dto.HotelDetailsReadDto;
import com.sspdev.hotelbooking.mapper.HotelDetailsCreateEditMapper;
import com.sspdev.hotelbooking.mapper.HotelDetailsReadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class HotelDetailsService {

    private final HotelDetailsRepository hotelDetailsRepository;
    private final HotelDetailsCreateEditMapper hotelDetailsCreateEditMapper;
    private final HotelDetailsReadMapper hotelDetailsReadMapper;

    @Transactional
    public Optional<HotelDetailsReadDto> update(Integer id, HotelDetailsCreateEditDto userDto) {
        return hotelDetailsRepository.findByHotelId(id)
                .map(entity -> hotelDetailsCreateEditMapper.map(userDto, entity))
                .map(hotelDetailsRepository::saveAndFlush)
                .map(hotelDetailsReadMapper::map);
    }
}