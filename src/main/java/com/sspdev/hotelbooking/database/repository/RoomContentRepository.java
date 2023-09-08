package com.sspdev.hotelbooking.database.repository;

import com.sspdev.hotelbooking.database.entity.RoomContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface RoomContentRepository extends
        JpaRepository<RoomContent, Integer>,
        QuerydslPredicateExecutor<RoomContent> {

    List<RoomContent> findByRoomId(Integer hotelId);
}