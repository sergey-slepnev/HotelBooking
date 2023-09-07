package com.sspdev.hotelbooking.dto;

public record HotelInfo(Integer id,
                        String name,
                        String star,
                        String link,
                        String type,
                        Double avgRating) {

}