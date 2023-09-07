package com.sspdev.hotelbooking.database.repository;

import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.dto.filter.RoomFilter;

import java.util.List;

public interface FilterRoomRepository {

    List<Room> findAllByFilter(RoomFilter filter);
}
