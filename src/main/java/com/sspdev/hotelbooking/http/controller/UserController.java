package com.sspdev.hotelbooking.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.PageResponse;
import com.sspdev.hotelbooking.dto.UserCreateEditDto;
import com.sspdev.hotelbooking.dto.UserReadDto;
import com.sspdev.hotelbooking.dto.filter.UserFilter;
import com.sspdev.hotelbooking.service.UserService;
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
@RequestMapping("/my-booking/users")
@SessionAttributes("user")
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

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") Integer id,
                           Model model) {
        return userService.findById(id)
                .map(user -> {
                    model.addAttribute("user", user);
                    return "user/user";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/registration")
    public String registration(Model model, @ModelAttribute("user") UserCreateEditDto createDto) {
        model.addAttribute("user", createDto);
        return "user/registration";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("user") @Validated UserCreateEditDto userCreateDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("user", userCreateDto);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/my-booking/users/registration";
        }
        userService.create(userCreateDto);

        return "redirect:/my-booking/users";
    }

    @GetMapping("/{id}/update")
    public String update(@SessionAttribute("user") UserReadDto user,
                         Model model) {
        model.addAttribute("user", user);
        return "user/update";
    }
}