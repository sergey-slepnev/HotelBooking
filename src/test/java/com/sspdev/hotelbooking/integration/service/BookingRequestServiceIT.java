package com.sspdev.hotelbooking.integration.service;

import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import com.sspdev.hotelbooking.service.BookingRequestService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@RequiredArgsConstructor
public class BookingRequestServiceIT extends IntegrationTestBase {

    private static final Long EXISTENT_REQUEST_ID = 1L;
    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer EXISTENT_ROOM_ID = 1;
    private static final Integer EXISTENT_USER_ID = 2;

    private final BookingRequestService bookingRequestService;

    @Test
    void findById_shouldFindRequestById_ifRequestExists() {
        var expectedBookingRequest = bookingRequestService.findById(EXISTENT_REQUEST_ID);

        expectedBookingRequest.ifPresent(request -> {
            assertAll(() -> {
                assertThat(request.createdAt()).isEqualTo("2022-10-10T12:05");
                assertThat(request.hotelId()).isEqualTo(EXISTENT_HOTEL_ID);
                assertThat(request.roomId()).isEqualTo(EXISTENT_ROOM_ID);
                assertThat(request.userId()).isEqualTo(EXISTENT_USER_ID);
                assertThat(request.checkIn()).isEqualTo("2022-10-10");
                assertThat(request.checkOut()).isEqualTo("2022-10-15");
                assertThat(request.status()).isEqualTo(Status.NEW);
            });
        });
    }
}