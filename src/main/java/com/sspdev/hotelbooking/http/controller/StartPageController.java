package com.sspdev.hotelbooking.http.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/my-booking")
public class StartPageController {

    @GetMapping
    public String login() {
        return "start_page";
    }
}