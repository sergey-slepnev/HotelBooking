package com.sspdev.hotelbooking.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.Star;
import com.sspdev.hotelbooking.dto.HotelContentCreateDto;
import com.sspdev.hotelbooking.dto.HotelCreateEditDto;
import com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto;
import com.sspdev.hotelbooking.dto.PageResponse;
import com.sspdev.hotelbooking.dto.UserReadDto;
import com.sspdev.hotelbooking.dto.filter.HotelFilter;
import com.sspdev.hotelbooking.service.HotelContentService;
import com.sspdev.hotelbooking.service.HotelDetailsService;
import com.sspdev.hotelbooking.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/my-booking/hotels")
@RequiredArgsConstructor
@SessionAttributes("hotel")
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

    @GetMapping
    public String findAll(Model model, HotelFilter filter, Pageable pageable) {
        var page = hotelService.findAllByFilter(filter, pageable);
        model.addAttribute("hotels", PageResponse.of(page));
        model.addAttribute("stars", Star.values());
        model.addAttribute("filter", filter);
        model.addAttribute("countries", hotelDetailsService.findCountries());
        return "hotel/hotels";
    }

    @GetMapping("/{userId}/add-hotel")
    public String add(@PathVariable("userId") Integer userId,
                      @SessionAttribute("user") UserReadDto user,
                      Model model,
                      @ModelAttribute("hotelCreateDto") HotelCreateEditDto hotelCreateDto,
                      @ModelAttribute("hotelDetails") HotelDetailsCreateEditDto hotelDetailsCreateDto,
                      @ModelAttribute("hotelContent") HotelContentCreateDto hotelContentCreateDto) {
        model.addAttribute("user", user);
        model.addAttribute("hotelCreateDto", hotelCreateDto);
        model.addAttribute("hotelDetails", hotelDetailsCreateDto);
        model.addAttribute("hotelContent", hotelContentCreateDto);
        model.addAttribute("stars", Star.values());

        return "hotel/add";
    }
}