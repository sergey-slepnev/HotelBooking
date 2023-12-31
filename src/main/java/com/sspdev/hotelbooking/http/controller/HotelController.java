package com.sspdev.hotelbooking.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.database.entity.enums.Star;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.HotelContentCreateDto;
import com.sspdev.hotelbooking.dto.HotelCreateEditDto;
import com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto;
import com.sspdev.hotelbooking.dto.HotelDetailsReadDto;
import com.sspdev.hotelbooking.dto.HotelReadDto;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/my-booking/hotels")
@RequiredArgsConstructor
@SessionAttributes({"hotel", "hotelDetails"})
public class HotelController {

    private final HotelService hotelService;
    private final HotelDetailsService hotelDetailsService;
    private final HotelContentService hotelContentService;

    @ModelAttribute("hotel")
    public HotelReadDto getHotel() {
        return new HotelReadDto();
    }

    @ModelAttribute("hotelDetails")
    public HotelDetailsReadDto getHotelDetails() {
        return new HotelDetailsReadDto();
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") Integer id,
                           @ModelAttribute("user") UserReadDto user,
                           Model model) {
        var maybeHotel = hotelService.findById(id);
        var maybeHotelDetails = hotelDetailsService.findByHotelId(id);
        if (maybeHotel.isPresent() && maybeHotelDetails.isPresent()) {
            model.addAttribute("user", user);
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

    @GetMapping("/{userId}/hotels-by-user")
    public String findAllByOwner(@PathVariable("userId") Integer userId,
                                 Model model) {
        var hotelsByUser = hotelService.findAllByOwnerId(userId);
        model.addAttribute("hotels", hotelsByUser);
        return "hotel/hotels_by_owner";
    }

    @GetMapping("/{userId}/add-hotel")
    public String add(@PathVariable("userId") Integer userId,
                      @SessionAttribute("user") UserReadDto user,
                      Model model,
                      @ModelAttribute("hotelCreateDto") HotelCreateEditDto hotelCreateDto,
                      @ModelAttribute("hotelDetailsCreateDto") HotelDetailsCreateEditDto hotelDetailsCreateDto,
                      @ModelAttribute("hotelContentCreateDto") HotelContentCreateDto hotelContentCreateDto) {
        model.addAttribute("user", user);
        model.addAttribute("hotelCreateDto", hotelCreateDto);
        model.addAttribute("hotelDetailsCreateDto", hotelDetailsCreateDto);
        model.addAttribute("hotelContentCreateDto", hotelContentCreateDto);
        model.addAttribute("stars", Star.values());

        return "hotel/add";
    }

    @PostMapping("/{userId}/create")
    public String create(@PathVariable("userId") Integer userId,
                         @ModelAttribute("hotelCreateDto") @Validated HotelCreateEditDto hotelCreateDto,
                         BindingResult hotelBindingResult,
                         @ModelAttribute("hotelDetails") @Validated HotelDetailsCreateEditDto hotelDetailsCreateDto,
                         BindingResult hotelDetailsBindingResult,
                         @ModelAttribute("hotelContent") @Validated HotelContentCreateDto hotelContentCreateDto,
                         BindingResult hotelContentBindingResult,
                         RedirectAttributes redirectAttributes) {
        if (hotelBindingResult.hasErrors() || hotelDetailsBindingResult.hasErrors() || hotelContentBindingResult.hasErrors()) {
            flashAttributes(hotelCreateDto,
                    hotelBindingResult,
                    hotelDetailsCreateDto,
                    hotelDetailsBindingResult,
                    hotelContentCreateDto,
                    hotelContentBindingResult,
                    redirectAttributes);
            return "redirect:/my-booking/hotels/" + userId + "/add-hotel";
        }

        var hotelReadDto = hotelService.create(hotelCreateDto, hotelDetailsCreateDto, hotelContentCreateDto);
        return "redirect:/my-booking/hotels/" + hotelReadDto.getId();
    }

    @GetMapping("{userId}/user-hotels/{hotelId}/edit")
    public String update(@PathVariable("hotelId") Integer hotelId,
                         @PathVariable("userId") Integer userId,
                         @SessionAttribute("user") UserReadDto user,
                         Model model) {
        hotelService.findById(hotelId).map(hotel -> model.addAttribute("hotel", hotel))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        hotelDetailsService.findByHotelId(hotelId).map(details -> model.addAttribute("hotelDetails", details))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("hotelContent", hotelContentService.findContent(hotelId));

        model.addAttribute("stars", Star.values());
        model.addAttribute("statuses", Status.values());
        model.addAttribute("contentTypes", ContentType.values());

        return "hotel/edit";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Integer id,
                         @SessionAttribute("user") UserReadDto user) {
        if (hotelService.delete(id)) {
            return "redirect:/my-booking/users/" + user.getId();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private static void flashAttributes(HotelCreateEditDto hotelDto,
                                        BindingResult hotelBindingResult,
                                        HotelDetailsCreateEditDto hotelDetailsCreateDTO,
                                        BindingResult hotelDetailsBindingResult,
                                        HotelContentCreateDto hotelContentCreateDTO,
                                        BindingResult hotelContentBindingResult,
                                        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("hotel", hotelDto);
        redirectAttributes.addFlashAttribute("hotelDetails", hotelDetailsCreateDTO);
        redirectAttributes.addFlashAttribute("hotelContent", hotelContentCreateDTO);
        redirectAttributes.addFlashAttribute("hotelErrors", hotelBindingResult.getAllErrors());
        redirectAttributes.addFlashAttribute("hotelDetailsErrors", hotelDetailsBindingResult.getAllErrors());
        redirectAttributes.addFlashAttribute("hotelContentErrors", hotelContentBindingResult.getAllErrors());
    }
}