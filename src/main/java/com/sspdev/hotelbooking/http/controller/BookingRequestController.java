package com.sspdev.hotelbooking.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.service.BookingRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/my-booking/booking-requests/")
@RequiredArgsConstructor
public class BookingRequestController {

    private final BookingRequestService bookingRequestService;

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id,
                           Model model) {
        return bookingRequestService.findById(id)
                .map(request -> {
                    model.addAttribute("request", request);
                    model.addAttribute("statuses", Status.values());
                    return "request/request";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}