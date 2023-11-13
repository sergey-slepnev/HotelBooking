package com.sspdev.hotelbooking.service;

import com.sspdev.hotelbooking.database.repository.HotelContentRepository;
import com.sspdev.hotelbooking.dto.HotelContentReadDto;
import com.sspdev.hotelbooking.mapper.HotelContentReadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelContentService {

    private final HotelContentRepository hotelContentRepository;
    private final HotelContentReadMapper hotelContentReadMapper;

    public List<HotelContentReadDto> findContent(Integer hotelId) {
        return hotelContentRepository.findByHotelId(hotelId).stream()
                .map(hotelContentReadMapper::map)
                .toList();
    }
}