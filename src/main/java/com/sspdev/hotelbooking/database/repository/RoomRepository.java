package com.sspdev.hotelbooking.database.repository;

import com.sspdev.hotelbooking.database.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface RoomRepository extends
        JpaRepository<Room, Integer>,
        FilterRoomRepository,
        QuerydslPredicateExecutor<Room> {

    List<Room> findByHotelId(Integer hotelId);
}