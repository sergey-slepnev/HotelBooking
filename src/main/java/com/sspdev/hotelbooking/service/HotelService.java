package com.sspdev.hotelbooking.service;

import com.sspdev.hotelbooking.database.repository.HotelRepository;
import com.sspdev.hotelbooking.dto.HotelCreateEditDto;
import com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto;
import com.sspdev.hotelbooking.dto.HotelReadDto;
import com.sspdev.hotelbooking.dto.filter.HotelFilter;
import com.sspdev.hotelbooking.mapper.HotelCreateEditMapper;
import com.sspdev.hotelbooking.mapper.HotelReadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelReadMapper hotelReadMapper;
    private final HotelCreateEditMapper hotelCreateEditMapper;
    private final HotelDetailsService hotelDetailsService;

    public List<HotelReadDto> findAllByFilter(HotelFilter filter) {
        return hotelRepository.findAllByFilter(filter).stream()
                .map(hotelReadMapper::map)
                .toList();
    }

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

    public Optional<HotelReadDto> findById(Integer id) {
        return hotelRepository.findById(id)
                .map(hotelReadMapper::map);
    }

    @Transactional
    public HotelReadDto create(HotelCreateEditDto hotelDto,
                               HotelDetailsCreateEditDto hotelDetailsDto) {
        return Optional.of(hotelDto)
                .map(hotelCreateEditMapper::map)
                .map(hotelRepository::save)
                .map(hotelReadMapper::map)
                .map(hotel -> {
                    hotelDetailsDto.setHotelId(hotel.getId());
                    hotelDetailsService.create(hotelDetailsDto);
                    return hotel;
                }).orElseThrow();
    }

    @Transactional
    public Optional<HotelReadDto> update(Integer id, HotelCreateEditDto hotelDto,
                                         HotelDetailsCreateEditDto hotelDetailsDto) {
        return hotelRepository.findById(id)
                .map(entity -> hotelCreateEditMapper.map(hotelDto, entity))
                .map(hotelRepository::saveAndFlush)
                .map(hotelReadMapper::map)
                .map(hotel -> {
                    hotelDetailsService.update(hotel.getId(), hotelDetailsDto);
                    return Optional.of(hotel);
                })
                .orElseThrow();
    }

    @Transactional
    public boolean delete(Integer id) {
        return hotelRepository.findById(id)
                .map(entity -> {
                    hotelRepository.delete(entity);
                    hotelRepository.flush();
                    return true;
                })
                .orElse(false);
    }
}