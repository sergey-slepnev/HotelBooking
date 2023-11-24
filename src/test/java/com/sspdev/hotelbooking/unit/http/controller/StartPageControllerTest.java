package com.sspdev.hotelbooking.unit.http.controller;

import com.sspdev.hotelbooking.http.controller.StartPageController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
public class StartPageControllerTest {

    private MockMvc mockMvc;
    @InjectMocks
    private StartPageController startPageController;

    public StartPageControllerTest() {
    }

    @BeforeEach
    public void initMock() {
        mockMvc = MockMvcBuilders.standaloneSetup(startPageController)
                .alwaysDo(print())
                .build();
    }

    @Test
    void registration_shouldReturnRegistrationPage() throws Exception {
        mockMvc.perform(get("/my-booking/registration"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("user/registration"))
                .andExpect(model().attributeExists("userCreateDto"));
    }
}