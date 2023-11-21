package com.sspdev.hotelbooking.service;

import com.sspdev.hotelbooking.database.entity.HotelContent;
import com.sspdev.hotelbooking.database.repository.HotelContentRepository;
import com.sspdev.hotelbooking.dto.HotelContentCreateDto;
import com.sspdev.hotelbooking.dto.HotelContentReadDto;
import com.sspdev.hotelbooking.mapper.HotelContentCreateMapper;
import com.sspdev.hotelbooking.mapper.HotelContentReadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelContentService {

    private final HotelContentRepository hotelContentRepository;
    private final HotelContentReadMapper hotelContentReadMapper;
    private final ApplicationContentService applicationContentService;
    private final HotelContentCreateMapper hotelContentCreateMapper;

    public List<HotelContentReadDto> findContent(Integer hotelId) {
        return hotelContentRepository.findByHotelId(hotelId).stream()
                .map(hotelContentReadMapper::map)
                .toList();
    }

    @Transactional
    public HotelContentReadDto save(HotelContentCreateDto contentCreateDto) {
        return Optional.of(contentCreateDto)
                .map(content -> {
                    var readContent = hotelContentCreateMapper.map(content);
                    applicationContentService.uploadImage(content.getContent());
                    return readContent;
                })
                .map(hotelContentRepository::save)
                .map(hotelContentReadMapper::map)
                .orElseThrow();
    }

    public void deleteAllByHotel(Integer hotelId) {
        var content = hotelContentRepository.findByHotelId(hotelId);
        content.stream()
                .map(HotelContent::getLink)
                .forEach(applicationContentService::deleteContentFromStore);
        hotelContentRepository.deleteAll(content);
        hotelContentRepository.flush();
    }
}