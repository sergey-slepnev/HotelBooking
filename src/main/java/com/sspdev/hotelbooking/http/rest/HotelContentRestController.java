package com.sspdev.hotelbooking.http.rest;

import com.sspdev.hotelbooking.dto.HotelContentReadDto;
import com.sspdev.hotelbooking.service.ApplicationContentService;
import com.sspdev.hotelbooking.service.HotelContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelContentRestController {

    private final HotelContentService hotelContentService;
    private final ApplicationContentService applicationContentService;

    @GetMapping("/{hotelId}/content/{contentId}")
    public ResponseEntity<byte[]> findContent(@PathVariable("hotelId") Integer hotelId,
                                              @PathVariable("contentId") Integer contentId) {
        return hotelContentService.findContent(hotelId)
                .stream()
                .filter(content -> content.getId().equals(contentId))
                .findFirst()
                .map(HotelContentReadDto::getLink)
                .filter(StringUtils::hasText)
                .map(applicationContentService::getImage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(content -> ok()
                        .contentLength(content.length)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .body(content))
                .orElseGet(notFound()::build);
    }
}