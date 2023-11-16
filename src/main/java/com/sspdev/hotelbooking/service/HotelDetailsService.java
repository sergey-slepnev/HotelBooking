package com.sspdev.hotelbooking.service;

import com.sspdev.hotelbooking.database.repository.HotelDetailsRepository;
import com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto;
import com.sspdev.hotelbooking.dto.HotelDetailsReadDto;
import com.sspdev.hotelbooking.mapper.HotelDetailsCreateEditMapper;
import com.sspdev.hotelbooking.mapper.HotelDetailsReadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class HotelDetailsService {

    private final HotelDetailsRepository hotelDetailsRepository;
    private final HotelDetailsCreateEditMapper hotelDetailsCreateEditMapper;
    private final HotelDetailsReadMapper hotelDetailsReadMapper;

    public Optional<HotelDetailsReadDto> findByHotelId(Integer hotelId) {
        return hotelDetailsRepository.findByHotelId(hotelId)
                .map(hotelDetailsReadMapper::map);
    }

    @Transactional
    public HotelDetailsReadDto create(HotelDetailsCreateEditDto createDTO) {
        return Optional.of(createDTO)
                .map(hotelDetailsCreateEditMapper::map)
                .map(hotelDetailsRepository::save)
                .map(hotelDetailsReadMapper::map)
                .orElseThrow();
    }

    @Transactional
    public Optional<HotelDetailsReadDto> update(Integer id, HotelDetailsCreateEditDto hotelDetails) {
        return hotelDetailsRepository.findById(id)
                .map(entity -> hotelDetailsCreateEditMapper.map(hotelDetails, entity))
                .map(hotelDetailsRepository::saveAndFlush)
                .map(hotelDetailsReadMapper::map);
    }

    public List<String> findCountries() {
        return hotelDetailsRepository.findCountries();
    }
}