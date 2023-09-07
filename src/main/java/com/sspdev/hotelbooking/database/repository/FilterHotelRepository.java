package com.sspdev.hotelbooking.database.repository;

import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.dto.HotelInfo;
import com.sspdev.hotelbooking.dto.filter.HotelFilter;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface FilterHotelRepository {

    @EntityGraph(attributePaths = "hotelDetails")
    List<Hotel> findAllByFilter(HotelFilter filter);

    List<HotelInfo> findTopFiveByRatingWithDetailsAndFirstPhoto();
}