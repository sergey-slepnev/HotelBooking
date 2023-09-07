package com.sspdev.hotelbooking.dto.filter;

import com.sspdev.hotelbooking.database.entity.enums.Star;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import lombok.Builder;

@Builder
public record HotelFilter(String name,
                          Status status,
                          Boolean available,
                          String country,
                          String locality,
                          Star star) {

}