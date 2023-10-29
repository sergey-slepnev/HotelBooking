package com.sspdev.hotelbooking.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import com.sspdev.hotelbooking.database.entity.enums.Star;
import com.sspdev.hotelbooking.dto.PageResponse;
import com.sspdev.hotelbooking.dto.filter.RoomFilter;
import com.sspdev.hotelbooking.service.HotelService;
import com.sspdev.hotelbooking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/my-booking/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final HotelService hotelService;

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") Integer id,
                           Model model) {
        return roomService.findById(id)
                .map(room -> {
                    model.addAttribute("room", room);
                    return "room/room";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/search")
    public String findAll(Model model, RoomFilter filter, Pageable pageable) {
        var roomPage = roomService.findAll(filter, pageable);
        model.addAttribute("rooms", PageResponse.of(roomPage));
        model.addAttribute("filter", filter);
        model.addAttribute("stars", Star.values());
        return "room/search";
    }

    @GetMapping("/{userId}/{hotelId}/add")
    public String create(@PathVariable("userId") Integer userId,
                         @PathVariable("hotelId") Integer hotelId,
                         Model model) {
        return hotelService.findById(hotelId)
                .map(hotel -> {
                    model.addAttribute("hotel", hotel);
                    model.addAttribute("types", RoomType.values());
                    model.addAttribute("stars", Star.values());
                    model.addAttribute("contentTypes", ContentType.values());
                    return "room/add";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}