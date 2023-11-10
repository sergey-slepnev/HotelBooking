package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.database.entity.RoomContent;
import com.sspdev.hotelbooking.database.repository.RoomRepository;
import com.sspdev.hotelbooking.dto.RoomContentCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static java.util.function.Predicate.not;

@Component
@RequiredArgsConstructor
public class RoomContentCreateMapper implements Mapper<RoomContentCreateDto, RoomContent> {

    private final RoomRepository roomRepository;

    @Override
    public RoomContent map(RoomContentCreateDto object) {
        var roomContent = new RoomContent();
        roomContent.setRoom(getRoom(object.getRoomId()));
        roomContent.setType(object.getContentType());

        Optional.ofNullable(object.getContent())
                .filter(not(MultipartFile::isEmpty))
                .ifPresent(image -> roomContent.setLink(image.getOriginalFilename()));

        return roomContent;
    }

    private Room getRoom(Integer id) {
        return Optional.ofNullable(id)
                .flatMap(roomRepository::findById)
                .orElse(null);
    }
}