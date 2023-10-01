package com.sspdev.hotelbooking.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.PageResponse;
import com.sspdev.hotelbooking.dto.filter.UserFilter;
import com.sspdev.hotelbooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/my-booking/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String findAll(Model model, UserFilter userFilter, Pageable pageable) {
        var page = userService.findAll(userFilter, pageable);
        model.addAttribute("users", PageResponse.of(page));
        model.addAttribute("filter", userFilter);
        model.addAttribute("roles", Role.values());
        model.addAttribute("statuses", Status.values());

        return "user/users";
    }
}