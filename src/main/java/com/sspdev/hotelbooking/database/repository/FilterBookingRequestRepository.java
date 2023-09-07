package com.sspdev.hotelbooking.database.repository;

import com.sspdev.hotelbooking.database.entity.BookingRequest;
import com.sspdev.hotelbooking.dto.filter.BookingRequestFilter;

import java.util.List;

public interface FilterBookingRequestRepository {

    List<BookingRequest> findAllByFilter(BookingRequestFilter filter);
}
