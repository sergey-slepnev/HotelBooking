package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.database.entity.RoomContent;
import com.sspdev.hotelbooking.database.repository.RoomRepository;
import com.sspdev.hotelbooking.dto.RoomContentCreateDto;
import com.sspdev.hotelbooking.service.ApplicationContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static java.util.function.Predicate.not;

@Component
@RequiredArgsConstructor
public class RoomContentCreateMapper implements Mapper<RoomContentCreateDto, RoomContent> {

    private final RoomRepository roomRepository;
    private final ApplicationContentService applicationContentService;

    @Override
    public RoomContent map(RoomContentCreateDto object) {
        var roomContent = new RoomContent();
        roomContent.setRoom(getRoom(object.getRoomId()));

        Optional.ofNullable(object.getContent())
                .filter(not(MultipartFile::isEmpty))
                .ifPresent(image -> {
                    roomContent.setLink(image.getOriginalFilename());
                    var contentType = applicationContentService.getContentType(image);
                    roomContent.setType(contentType);
                });

        return roomContent;
    }

    private Room getRoom(Integer id) {
        return Optional.ofNullable(id)
                .flatMap(roomRepository::findById)
                .orElse(null);
    }
}