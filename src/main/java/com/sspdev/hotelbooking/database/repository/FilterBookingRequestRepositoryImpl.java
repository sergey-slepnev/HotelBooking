package com.sspdev.hotelbooking.database.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.sspdev.hotelbooking.database.entity.BookingRequest;
import com.sspdev.hotelbooking.database.querydsl.QPredicates;
import com.sspdev.hotelbooking.dto.filter.BookingRequestFilter;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.sspdev.hotelbooking.database.entity.QBookingRequest.bookingRequest;

@RequiredArgsConstructor
public class FilterBookingRequestRepositoryImpl implements FilterBookingRequestRepository {

    private final EntityManager entityManager;

    @Override
    public List<BookingRequest> findAllByFilter(BookingRequestFilter filter) {
        var predicate = QPredicates.builder()
                .add(filter.createdAtFrom(), bookingRequest.createdAt::goe)
                .add(filter.createdAtTo(), bookingRequest.createdAt::loe)
                .add(filter.hotelName(), bookingRequest.hotel.name::eq)
                .add(filter.status(), bookingRequest.status::eq)
                .build();

        return new JPAQuery<BookingRequest>(entityManager)
                .select(bookingRequest)
                .from(bookingRequest)
                .where(predicate)
                .orderBy(bookingRequest.createdAt.desc())
                .fetch();
    }
}