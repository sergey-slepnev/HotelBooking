package com.sspdev.hotelbooking.database.repository;

import com.sspdev.hotelbooking.database.entity.HotelDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface HotelDetailsRepository extends
        JpaRepository<HotelDetails, Integer>,
        QuerydslPredicateExecutor<HotelDetails> {

    Optional<HotelDetails> findByHotelId(Integer hotelId);

    @Query("select distinct hd.country from HotelDetails hd")
    List<String> findCountries();

    @Query("select distinct hd.locality from HotelDetails hd")
    List<String> findLocalities();
}