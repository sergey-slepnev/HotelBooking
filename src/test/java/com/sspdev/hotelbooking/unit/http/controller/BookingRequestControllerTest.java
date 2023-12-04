package com.sspdev.hotelbooking.unit.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.BookingRequestReadDto;
import com.sspdev.hotelbooking.http.controller.BookingRequestController;
import com.sspdev.hotelbooking.service.BookingRequestService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RequiredArgsConstructor
@ExtendWith(SpringExtension.class)
class BookingRequestControllerTest {

    private static final Long EXISTENT_REQUEST_ID = 1L;
    private static final Long NOT_EXISTENT_REQUEST_ID = 999L;

    private MockMvc mockMvc;
    @MockBean
    private final BookingRequestService bookingRequestService;
    @InjectMocks
    private BookingRequestController bookingRequestController;

    @BeforeEach
    public void initMockMvc() {
        mockMvc = standaloneSetup(bookingRequestController)
                .alwaysDo(print())
                .build();
    }

    @Test
    void findById_shouldFindBookingRequest_whenExist() throws Exception {
        var requestReadDto = getBookingRequestReadDto();
        when(bookingRequestService.findById(EXISTENT_REQUEST_ID)).thenReturn(Optional.of(requestReadDto));
        mockMvc.perform(get("/my-booking/booking-requests/" + EXISTENT_REQUEST_ID))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("request", "statuses"));
    }

    @Test
    void findById_shouldReturn404_whenRequestNotExist() throws Exception {
        when(bookingRequestService.findById(EXISTENT_REQUEST_ID)).thenReturn(Optional.empty());
        mockMvc.perform(get("/my-booking/booking-requests/" + NOT_EXISTENT_REQUEST_ID))
                .andExpect(status().isNotFound());
    }

    private BookingRequestReadDto getBookingRequestReadDto() {
        return BookingRequestReadDto.builder()
                .id(EXISTENT_REQUEST_ID)
                .createdAt(LocalDateTime.of(2023, 12, 1, 15, 15))
                .hotelName("MoscowPlaza")
                .roomNo(1)
                .userName("TestUser")
                .firstName("TestFirstName")
                .lastName("TestLastName")
                .checkIn(LocalDate.of(2023, 12, 1))
                .checkOut(LocalDate.of(2023, 12, 10))
                .daysToStay(9L)
                .costPerDay(BigDecimal.valueOf(1500))
                .totalCost(BigDecimal.valueOf(17100))
                .status(Status.NEW)
                .build();
    }
}