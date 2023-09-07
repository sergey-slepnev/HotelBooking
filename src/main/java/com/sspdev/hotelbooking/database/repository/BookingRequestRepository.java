package com.sspdev.hotelbooking.database.repository;

import com.sspdev.hotelbooking.database.entity.BookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface BookingRequestRepository extends
        JpaRepository<BookingRequest, Long>,
        FilterBookingRequestRepository,
        QuerydslPredicateExecutor<BookingRequest> {

    @Query(value = "select count(br) from BookingRequest br")
    Long countAllRequest();
}