package com.sspdev.hotelbooking.service;

import com.sspdev.hotelbooking.database.repository.RoomContentRepository;
import com.sspdev.hotelbooking.dto.RoomContentCreateDto;
import com.sspdev.hotelbooking.dto.RoomContentReadDto;
import com.sspdev.hotelbooking.mapper.RoomContentCreateMapper;
import com.sspdev.hotelbooking.mapper.RoomContentReadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RoomContentService {

    private final RoomContentRepository roomContentRepository;
    private final RoomContentCreateMapper roomContentCreateMapper;
    private final RoomContentReadMapper roomContentReadMapper;
    private final ApplicationContentService applicationContentService;

    @Transactional
    public RoomContentReadDto save(RoomContentCreateDto contentCreateDto) {
        return Optional.of(contentCreateDto)
                .map(content -> {
                    applicationContentService.uploadImage(content.getContent());
                    return roomContentCreateMapper.map(content);
                })
                .map(roomContentRepository::save)
                .map(roomContentReadMapper::map)
                .orElseThrow();
    }

    public List<RoomContentReadDto> findByRoom(Integer id) {
        return roomContentRepository.findByRoomId(id).stream()
                .map(roomContentReadMapper::map)
                .toList();
    }

    public boolean delete(Integer id) {
        return roomContentRepository.findById(id)
                .map(content -> {
                    roomContentRepository.delete(content);
                    roomContentRepository.flush();
                    return true;
                })
                .orElse(false);
    }
}