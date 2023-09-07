package com.sspdev.hotelbooking.integration.repository;

import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.database.repository.BookingRequestRepository;
import com.sspdev.hotelbooking.dto.filter.BookingRequestFilter;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class BookingRequestRepositoryIT extends IntegrationTestBase {

    private static final Integer NO_PREDICATE_FILTER_REQUEST_COLLECTION_SIZE = 9;
    private static final Integer CREATED_AT_FROM_TO_HOTEL_NAME_STATUS_FILTER_COLLECTION_SIZE = 2;

    private final BookingRequestRepository bookingRequestRepository;

    @Test
    void checkFindAllByFilterWithNoPredicates() {
        var noPredicateFilter = BookingRequestFilter.builder().build();
        var noPredicateRequests = bookingRequestRepository.findAllByFilter(noPredicateFilter);

        assertThat(noPredicateRequests).hasSize(NO_PREDICATE_FILTER_REQUEST_COLLECTION_SIZE);

        var requestCreatedDates = noPredicateRequests.stream()
                .map(request -> request.getCreatedAt().toLocalDate().toString()).toList();

        assertThat(requestCreatedDates).containsExactly(
                "2022-10-18",
                "2022-10-17",
                "2022-10-16",
                "2022-10-15",
                "2022-10-14",
                "2022-10-13",
                "2022-10-12",
                "2022-10-11",
                "2022-10-10");
    }

    @Test
    void checkFindAllFilteredByCreatedAtFromToHotelNameStatus() {
        var availableCostFromToFilter = BookingRequestFilter.builder()
                .createdAtFrom(LocalDateTime.of(2022, 10, 11, 0, 0))
                .createdAtTo(LocalDateTime.of(2022, 10, 17, 0, 0))
                .hotelName("PiterPlaza")
                .status(Status.APPROVED)
                .build();

        var filteredRequests = bookingRequestRepository.findAllByFilter(availableCostFromToFilter);

        assertThat(filteredRequests).hasSize(CREATED_AT_FROM_TO_HOTEL_NAME_STATUS_FILTER_COLLECTION_SIZE);
    }
}