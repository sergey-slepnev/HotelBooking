package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.HotelContent;
import com.sspdev.hotelbooking.database.repository.HotelRepository;
import com.sspdev.hotelbooking.dto.HotelContentCreateDto;
import com.sspdev.hotelbooking.service.ApplicationContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static java.util.function.Predicate.not;

@Component
@RequiredArgsConstructor
public class HotelContentCreateMapper implements Mapper<HotelContentCreateDto, HotelContent> {

    private final HotelRepository hotelRepository;
    private final ApplicationContentService applicationContentService;

    @Override
    public HotelContent map(HotelContentCreateDto createDto) {
        var hotelContent = new HotelContent();
        hotelContent.setHotel(getHotel(createDto.getHotelId()));
        Optional.ofNullable(createDto.getContent())
                .filter(not(MultipartFile::isEmpty))
                .ifPresent(content -> {
                    hotelContent.setLink(content.getOriginalFilename());
                    hotelContent.setType(applicationContentService.getContentType(content));
                });

        return hotelContent;
    }

    private Hotel getHotel(Integer hotelId) {
        return Optional.ofNullable(hotelId)
                .flatMap(hotelRepository::findById)
                .orElse(null);
    }
}