package com.sspdev.hotelbooking.service;

import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.database.querydsl.QPredicates;
import com.sspdev.hotelbooking.database.repository.UserRepository;
import com.sspdev.hotelbooking.dto.UserCreateEditDto;
import com.sspdev.hotelbooking.dto.UserReadDto;
import com.sspdev.hotelbooking.dto.filter.UserFilter;
import com.sspdev.hotelbooking.mapper.UserCreateEditMapper;
import com.sspdev.hotelbooking.mapper.UserReadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.sspdev.hotelbooking.database.entity.QUser.user;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserReadMapper userReadMapper;
    private final UserCreateEditMapper userCreateEditMapper;
    private final ApplicationContentService applicationContentService;

    public List<UserReadDto> findAll() {
        return userRepository.findAll().stream()
                .map(userReadMapper::map)
                .toList();
    }

    public Page<UserReadDto> findAll(UserFilter filter, Pageable pageable) {
        var predicate = QPredicates.builder()
                .add(filter.role(), user.role::eq)
                .add(filter.firstName(), user.personalInfo.firstname::containsIgnoreCase)
                .add(filter.lastName(), user.personalInfo.lastname::containsIgnoreCase)
                .add(filter.status(), user.status::eq)
                .build();

        return userRepository.findAll(predicate, pageable)
                .map(userReadMapper::map);
    }

    public List<UserReadDto> findAllByFilter(UserFilter userFilter) {
        return userRepository.findAllByFilter(userFilter).stream()
                .map(userReadMapper::map)
                .toList();
    }

    public Optional<UserReadDto> findById(Integer userId) {
        return userRepository.findById(userId)
                .map(userReadMapper::map);
    }

    @Transactional
    public UserReadDto create(UserCreateEditDto createDto) {
        return Optional.of(createDto)
                .map(dto -> {
                    applicationContentService.uploadImage(dto.getImage());
                    return userCreateEditMapper.map(dto);
                })
                .map(userRepository::save)
                .map(userReadMapper::map)
                .orElseThrow();
    }

    @Transactional
    public Optional<UserReadDto> update(Integer id, UserCreateEditDto updateDto) {
        return userRepository.findById(id)
                .map(entity -> {
                    applicationContentService.uploadImage(updateDto.getImage());
                    return userCreateEditMapper.map(updateDto, entity);
                })
                .map(userRepository::saveAndFlush)
                .map(userReadMapper::map);
    }

    @Transactional
    public boolean delete(Integer id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    userRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public void changeStatus(Status status, Integer userId) {
        userRepository.changeStatusByUserId(status, userId);
        userRepository.flush();
    }

    public Optional<byte[]> findImage(Integer userId) {
        return userRepository.findById(userId)
                .map(User::getImage)
                .filter(StringUtils::hasText)
                .flatMap(applicationContentService::getImage);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user ->
                        new org.springframework.security.core.userdetails.User(
                                user.getUsername(),
                                user.getPassword(),
                                Collections.singleton(user.getRole())))
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user: " + username));
    }
}