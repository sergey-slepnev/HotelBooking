package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.dto.RoomReadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RoomReadMapper implements Mapper<Room, RoomReadDto> {

    private final RoomContentReadMapper roomContentReadMapper;

    @Override
    public RoomReadDto map(Room object) {
        return new RoomReadDto(
                object.getId(),
                object.getHotel().getId(),
                object.getRoomNo(),
                object.getType(),
                object.getSquare(),
                object.getAdultBedCount(),
                object.getChildrenBedCount(),
                object.getCost(),
                object.getFloor(),
                object.isAvailable(),
                object.getDescription(),
                object.getRoomContents().stream().map(roomContentReadMapper::map).toList()
        );
    }
}