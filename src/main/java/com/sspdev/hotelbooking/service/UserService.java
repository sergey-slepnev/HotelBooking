package com.sspdev.hotelbooking.service;

import com.sspdev.hotelbooking.database.repository.UserRepository;
import com.sspdev.hotelbooking.dto.UserCreateEditDto;
import com.sspdev.hotelbooking.dto.UserReadDto;
import com.sspdev.hotelbooking.mapper.UserCreateEditMapper;
import com.sspdev.hotelbooking.mapper.UserReadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserReadMapper userReadMapper;
    private final UserCreateEditMapper userCreateEditMapper;

    public List<UserReadDto> findAll() {
        return userRepository.findAll().stream()
                .map(userReadMapper::map)
                .toList();
    }

    public Optional<UserReadDto> findById(Integer userId) {
        return userRepository.findById(userId)
                .map(userReadMapper::map);
    }

    @Transactional
    public UserReadDto create(UserCreateEditDto createEditDto) {
        return Optional.of(createEditDto)
                .map(userCreateEditMapper::map)
                .map(userRepository::save)
                .map(userReadMapper::map)
                .orElseThrow();
    }

    @Transactional
    public Optional<UserReadDto> update(Integer id, UserCreateEditDto createEditDto) {
        return userRepository.findById(id)
                .map(entity -> userCreateEditMapper.map(createEditDto, entity))
                .map(userRepository::saveAndFlush)
                .map(userReadMapper::map);
    }
}