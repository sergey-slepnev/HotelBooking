package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.HotelDetails;
import com.sspdev.hotelbooking.database.repository.HotelRepository;
import com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HotelDetailsCreateEditMapper implements Mapper<HotelDetailsCreateEditDto, HotelDetails> {

    private final HotelRepository hotelRepository;

    @Override
    public HotelDetails map(HotelDetailsCreateEditDto object) {
        var hotelDetails = new HotelDetails();
        copy(object, hotelDetails);
        return hotelDetails;
    }

    @Override
    public HotelDetails map(HotelDetailsCreateEditDto fromObject, HotelDetails toObject) {
        copy(fromObject, toObject);
        return toObject;
    }

    private void copy(HotelDetailsCreateEditDto object, HotelDetails hotelDetails) {
        hotelDetails.setHotel(getHotel(object.getHotelId()));
        hotelDetails.setPhoneNumber(object.getPhoneNumber());
        hotelDetails.setCountry(object.getCountry());
        hotelDetails.setLocality(object.getLocality());
        hotelDetails.setArea(object.getArea());
        hotelDetails.setStreet(object.getStreet());
        hotelDetails.setFloorCount(object.getFloorCount());
        hotelDetails.setStar(object.getStar());
        hotelDetails.setDescription(object.getDescription());
    }

    private Hotel getHotel(Integer hotelId) {
        return Optional.ofNullable(hotelId)
                .flatMap(hotelRepository::findById)
                .orElse(null);
    }
}