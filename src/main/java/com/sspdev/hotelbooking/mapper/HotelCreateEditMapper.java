package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.database.repository.UserRepository;
import com.sspdev.hotelbooking.dto.HotelCreateEditDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HotelCreateEditMapper implements Mapper<HotelCreateEditDto, Hotel> {

    private final UserRepository userRepository;

    @Override
    public Hotel map(HotelCreateEditDto object) {
        var hotel = new Hotel();
        hotel.setOwner(getUser(object.getOwnerId()));
        hotel.setName(object.getName());
        hotel.setAvailable(false);
        hotel.setStatus(Status.NEW);

        return hotel;
    }

    private User getUser(Integer id) {
        return Optional.ofNullable(id)
                .flatMap(userRepository::findById)
                .orElse(null);
    }
}