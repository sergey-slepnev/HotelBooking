package com.sspdev.hotelbooking.integration.service;

import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import com.sspdev.hotelbooking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
public class RoomServiceIT extends IntegrationTestBase {

    private static final Integer EXISTENT_HOTEL_ID = 1;

    private final RoomService roomService;

    @Test
    void findById_shouldFindHotelById_whenHotelExist() {
        var maybeHotel = roomService.findById(EXISTENT_HOTEL_ID);

        assertThat(maybeHotel).isPresent();
        maybeHotel.ifPresent(hotel ->
                assertAll(() -> {
                    assertEquals(hotel.getRoomNo(), 1);
                    assertEquals(hotel.getSquare(), 25.3);
                    assertEquals(hotel.getAdultBedCount(), 3);
                    assertEquals(hotel.getChildrenBedCount(), 0);
                    assertEquals(hotel.getCost(), BigDecimal.valueOf(1500.99));
                    assertEquals(hotel.getFloor(), 1);
                }));
    }
}
