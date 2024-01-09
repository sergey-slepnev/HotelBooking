package com.sspdev.hotelbooking.integration.service;

import com.sspdev.hotelbooking.database.entity.BookingRequest;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.BookingRequestCreateEditDto;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import com.sspdev.hotelbooking.service.BookingRequestService;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@RequiredArgsConstructor
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingRequestServiceIT extends IntegrationTestBase {

    private static final Long EXISTENT_REQUEST_ID = 1L;
    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer EXISTENT_ROOM_ID = 1;
    private static final Integer EXISTENT_USER_ID = 2;
    private static final Integer FIRST_OWNER_ID = 4;

    private final BookingRequestService bookingRequestService;
    private final SessionFactory sessionFactory;

    @Test
    void findById_shouldFindRequestById_ifRequestExists() {
        var expectedBookingRequest = bookingRequestService.findById(EXISTENT_REQUEST_ID);

        expectedBookingRequest.ifPresent(request -> {
            assertAll(() -> {
                assertThat(request.createdAt()).isEqualTo("2022-10-10T12:05");
                assertThat(request.hotelName()).isEqualTo("MoscowPlaza");
                assertThat(request.roomNo()).isEqualTo(1);
                assertThat(request.userName()).isEqualTo("FirstUser@gmail.com");
                assertThat(request.checkIn()).isEqualTo("2022-10-10");
                assertThat(request.checkOut()).isEqualTo("2022-10-15");
                assertThat(request.status()).isEqualTo(Status.NEW);
            });
        });
    }

    @Test
    void create_shouldCreateNewBookingRequest() {
        var createDto = getBookingRequestCreateDto();
        var actualReadDto = bookingRequestService.create(createDto);

        assertThat(actualReadDto.id()).isNotNull();
        assertThat(actualReadDto.createdAt()).isEqualTo("2023-12-01T15:15");
        assertThat(actualReadDto.hotelName()).isEqualTo("MoscowPlaza");
        assertThat(actualReadDto.roomNo()).isEqualTo(1);
        assertThat(actualReadDto.userName()).isEqualTo("FirstUser@gmail.com");
        assertThat(actualReadDto.checkIn()).isEqualTo("2023-12-01");
        assertThat(actualReadDto.checkOut()).isEqualTo("2023-12-10");
        assertThat(actualReadDto.status()).isEqualTo(Status.NEW);
    }

    @Test
    void getTotalRequests_shouldReturnAllRequestsFromDb() {
        var actualResult = bookingRequestService.getTotalRequests();

        assertThat(actualResult).isEqualTo(9);
    }

    @Test
    void countRequestsByStatus_shouldReturnAllRequestsInMapWithStatusAndCount() {
        var actualStatusesAndCount = bookingRequestService.countRequestsByStatus();

        assertThat(actualStatusesAndCount).containsAllEntriesOf(Map.of(
                "NEW", 2L,
                "APPROVED", 3L,
                "PAID", 2L,
                "CANCELED", 2L));
    }

    @Test
    void countRequestsByUserAndStatus_shouldReturnRequestByUserMapperStatusAndCount() {
        var actualStatusesAndCount = bookingRequestService.countRequestsByUserAndStatus(EXISTENT_USER_ID);

        assertThat(actualStatusesAndCount).containsAllEntriesOf(Map.of(
                "NEW", 2L,
                "APPROVED", 2L,
                "PAID", 1L
        ));
    }

    @Test
    void countRequestsByOwnerAndStatus_shouldReturnRequestByOwnerMappedStatusesAndCount() {
        var requestForFirstOwner = bookingRequestService.countRequestsByOwnerAndStatus(FIRST_OWNER_ID);

        assertThat(requestForFirstOwner).containsAllEntriesOf(Map.of(
                "NEW", 2L,
                "CANCELED", 2L
        ));
    }

    @Test
    @Order(1)
    void checkSecondLevelCache() {
        var firstSession = sessionFactory.openSession();
        var firstTransaction = firstSession.getTransaction();
        firstTransaction.begin();
        firstSession.get(BookingRequest.class, 1);
        firstTransaction.commit();

        var secondSession = sessionFactory.openSession();
        var secondTransaction = secondSession.getTransaction();
        secondTransaction.begin();
        secondSession.get(BookingRequest.class, 1);
        secondTransaction.commit();

        firstSession.close();
        secondSession.close();
    }

    private BookingRequestCreateEditDto getBookingRequestCreateDto() {
        return BookingRequestCreateEditDto.builder()
                .createdAt(LocalDateTime.of(2023, 12, 1, 15, 15))
                .hotelId(EXISTENT_HOTEL_ID)
                .roomId(EXISTENT_ROOM_ID)
                .userId(EXISTENT_USER_ID)
                .checkIn(LocalDate.of(2023, 12, 1))
                .checkOut(LocalDate.of(2023, 12, 10))
                .status(Status.NEW)
                .build();
    }
}