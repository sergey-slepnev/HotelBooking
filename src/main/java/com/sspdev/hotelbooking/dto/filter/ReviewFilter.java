package com.sspdev.hotelbooking.dto.filter;

import com.sspdev.hotelbooking.database.entity.enums.Rating;
import lombok.Builder;

@Builder
public record ReviewFilter(String hotelName,
                           Rating ratingFrom,
                           Rating ratingTo) {

}
