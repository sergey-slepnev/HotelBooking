package com.sspdev.hotelbooking.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import com.sspdev.hotelbooking.database.entity.enums.Star;
import com.sspdev.hotelbooking.dto.HotelReadDto;
import com.sspdev.hotelbooking.dto.PageResponse;
import com.sspdev.hotelbooking.dto.RoomContentCreateDto;
import com.sspdev.hotelbooking.dto.RoomCreateEditDto;
import com.sspdev.hotelbooking.dto.RoomReadDto;
import com.sspdev.hotelbooking.dto.filter.RoomFilter;
import com.sspdev.hotelbooking.service.HotelService;
import com.sspdev.hotelbooking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/my-booking/rooms")
@SessionAttributes(names = "room", types = RoomReadDto.class)
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

    @PostMapping("/{userId}/{hotelId}/create")
    public String create(@PathVariable("userId") Integer userId,
                         @PathVariable("hotelId") Integer hotelId,
                         RoomContentCreateDto contentCreateDto,
                         @Validated RoomCreateEditDto createRoomDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("room", createRoomDto);
            redirectAttributes.addFlashAttribute("roomContent", contentCreateDto);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/my-booking/rooms/{userId}/{hotelId}/add";
        }
        var createdRoom = roomService.create(createRoomDto, contentCreateDto);
        return "redirect:/my-booking/rooms/" + createdRoom.getId();
    }

    @GetMapping("/{hotelId}/rooms-by-hotel")
    public String findByHotel(@PathVariable("hotelId") Integer hotelId,
                              Model model) {
        var roomsByHotel = roomService.findByHotel(hotelId);
        model.addAttribute("rooms", roomsByHotel);
        return "room/rooms-by-hotel";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Integer id,
                         @SessionAttribute("hotel") HotelReadDto hotel) {
        if (roomService.delete(id)) {
            return "redirect:/my-booking/hotels/" + hotel.getId();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}