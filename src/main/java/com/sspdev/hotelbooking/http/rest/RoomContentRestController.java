package com.sspdev.hotelbooking.http.rest;

import com.sspdev.hotelbooking.dto.RoomContentReadDto;
import com.sspdev.hotelbooking.service.ApplicationContentService;
import com.sspdev.hotelbooking.service.RoomContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                .map(content -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .contentLength(content.length)
                        .body(content))
                .orElseGet(ResponseEntity.notFound()::build);
    }
}