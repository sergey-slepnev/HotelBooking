package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.database.repository.HotelRepository;
import com.sspdev.hotelbooking.dto.RoomCreateEditDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoomCreateEditMapper implements Mapper<RoomCreateEditDto, Room> {

    private final HotelRepository hotelRepository;

    @Override
    public Room map(RoomCreateEditDto object) {
        var room = new Room();
        copy(object, room);

        return room;
    }

    @Override
    public Room map(RoomCreateEditDto fromObject, Room toObject) {
        copy(fromObject, toObject);
        return toObject;
    }

    private void copy(RoomCreateEditDto object, Room room) {
        room.setHotel(getHotel(object.hotelId()));
        room.setRoomNo(object.roomNo());
        room.setType(object.type());
        room.setSquare(object.square());
        room.setAdultBedCount(object.adultBedCount());
        room.setChildrenBedCount(object.childrenBedCount());
        room.setCost(object.cost());
        room.setAvailable(true);
        room.setFloor(object.floor());
        room.setDescription(object.description());
    }

    private Hotel getHotel(Integer hotelId) {
        return Optional.ofNullable(hotelId)
                .flatMap(hotelRepository::findById)
                .orElse(null);
    }
}