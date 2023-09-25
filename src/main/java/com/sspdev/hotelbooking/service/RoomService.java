package com.sspdev.hotelbooking.service;

import com.sspdev.hotelbooking.database.repository.RoomRepository;
import com.sspdev.hotelbooking.dto.RoomReadDto;
import com.sspdev.hotelbooking.mapper.RoomReadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomReadMapper roomReadMapper;

    public Optional<RoomReadDto> findById(Integer roomId) {
        return roomRepository.findById(roomId)
                .map(roomReadMapper::map);
    }
}
