package com.sspdev.hotelbooking.http.controller;

import com.sspdev.hotelbooking.service.HotelContentService;
import com.sspdev.hotelbooking.service.HotelDetailsService;
import com.sspdev.hotelbooking.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/my-booking/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final HotelDetailsService hotelDetailsService;
    private final HotelContentService hotelContentService;

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") Integer id,
                           Model model) {
        var maybeHotel = hotelService.findById(id);
        var maybeHotelDetails = hotelDetailsService.findByHotelId(id);
        if (maybeHotel.isPresent() && maybeHotelDetails.isPresent()) {
            maybeHotel.map(hotel -> model.addAttribute("hotel", hotel));
            maybeHotelDetails.map(hotelDetails -> model.addAttribute("hotelDetails", hotelDetails));
            model.addAttribute("contents", hotelContentService.findContent(id));
            return "hotel/hotel";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}