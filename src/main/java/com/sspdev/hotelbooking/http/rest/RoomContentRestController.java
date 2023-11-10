package com.sspdev.hotelbooking.http.rest;

import com.sspdev.hotelbooking.dto.RoomContentCreateDto;
import com.sspdev.hotelbooking.dto.RoomContentReadDto;
import com.sspdev.hotelbooking.dto.RoomReadDto;
import com.sspdev.hotelbooking.service.ApplicationContentService;
import com.sspdev.hotelbooking.service.RoomContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.net.URI;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rooms")
public class RoomContentRestController {

    private final RoomContentService roomContentService;
    private final ApplicationContentService applicationContentService;

    @GetMapping(value = "/{roomId}/content/{contentId}")
    public ResponseEntity<byte[]> getImage(@PathVariable("roomId") Integer roomId,
                                           @PathVariable("contentId") Integer contentId) {
        return roomContentService.findByRoom(roomId).stream()
                .filter(content -> content.getId().equals(contentId))
                .findFirst()
                .map(RoomContentReadDto::getLink)
                .filter(StringUtils::hasText)
                .map(imageName -> applicationContentService.getImage(imageName).get())
                .map(content -> ok()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .contentLength(content.length)
                        .body(content))
                .orElseGet(notFound()::build);
    }

    @PostMapping("/content/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id,
                                    @SessionAttribute("room") RoomReadDto room) {
        return roomContentService.delete(id)
                ? status(HttpStatus.FOUND)
                .location(URI.create("/my-booking/rooms/" + room.getId()))
                .build()
                : notFound().build();
    }

    @PostMapping("/{roomId}/content/create")
    public ResponseEntity<RoomContentReadDto> create(@PathVariable("roomId") Integer roomId,
                                                     @ModelAttribute("content") RoomContentCreateDto contentCreateDto) {
        ResponseEntity<RoomContentReadDto> response = status(HttpStatus.FOUND)
                .location(URI.create("/my-booking/rooms/" + roomId))
                .build();

        if (!contentCreateDto.getContent().isEmpty()) {
            contentCreateDto.setRoomId(roomId);
            roomContentService.save(contentCreateDto);
            return response;
        } else {
            return response;
        }
    }
}