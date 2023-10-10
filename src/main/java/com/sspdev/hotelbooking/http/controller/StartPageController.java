package com.sspdev.hotelbooking.http.controller;

import com.sspdev.hotelbooking.database.repository.HotelRepository;
import com.sspdev.hotelbooking.dto.UserCreateEditDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/my-booking")
@RequiredArgsConstructor
public class StartPageController {

    private final HotelRepository hotelRepository;

    @GetMapping
    public String login(Model model) {
        var topFiveHotels = hotelRepository.findTopFiveByRatingWithDetailsAndFirstPhoto();
        model.addAttribute("topFiveHotels", topFiveHotels);
        return "start_page";
    }

    @GetMapping("/registration")
    public String registration(Model model, @ModelAttribute("userCreateDto") UserCreateEditDto userCreateDto) {
        model.addAttribute("userCreateDto", userCreateDto);
        return "user/registration";
    }
}