package com.sspdev.hotelbooking.database.repository;

import com.sspdev.hotelbooking.database.entity.BookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface BookingRequestRepository extends
        JpaRepository<BookingRequest, Long>,
        FilterBookingRequestRepository,
        QuerydslPredicateExecutor<BookingRequest> {

    @Query("select br from BookingRequest br where br.user.id= :id")
    List<BookingRequest> findByUser(Integer id);

    @Query("select br " +
            "from BookingRequest br " +
            "left join Hotel h ON br.hotel.id = h.id " +
            "where h.owner.id = :id")
    List<BookingRequest> countRequestsByOwnerAndStatus(Integer id);
}