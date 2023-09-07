package com.sspdev.hotelbooking.database.repository;

import com.sspdev.hotelbooking.database.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface HotelRepository extends
        JpaRepository<Hotel, Integer>,
        FilterHotelRepository,
        QuerydslPredicateExecutor<Hotel> {

    List<Hotel> findAllByOwnerId(Integer ownerId);

}