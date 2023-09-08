package com.sspdev.hotelbooking.dto;

public record HotelRoomInfo(Integer hotelId,
                            String hotelName,
                            Integer availableRooms,
                            String link) {

}