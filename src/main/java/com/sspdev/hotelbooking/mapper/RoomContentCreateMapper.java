package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.database.entity.RoomContent;
import com.sspdev.hotelbooking.database.repository.RoomRepository;
import com.sspdev.hotelbooking.dto.RoomContentCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoomContentCreateMapper implements Mapper<RoomContentCreateDto, RoomContent> {

    private final RoomRepository roomRepository;

    @Override
    public RoomContent map(RoomContentCreateDto object) {
        var roomContent = new RoomContent();
        roomContent.setRoom(getRoom(object.getRoomId()));
        roomContent.setLink(object.getContent().getOriginalFilename());
        roomContent.setType(object.getContentType());

        return roomContent;
    }

    private Room getRoom(Integer id) {
        return Optional.ofNullable(id)
                .flatMap(roomRepository::findById)
                .orElse(null);
    }
}
