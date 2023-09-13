package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.dto.UserReadDto;
import org.springframework.stereotype.Component;

@Component
public class UserReadMapper implements Mapper<User, UserReadDto> {

    @Override
    public UserReadDto map(User object) {
        return new UserReadDto(
                object.getId(),
                object.getRole(),
                object.getUsername(),
                object.getPassword(),
                object.getPersonalInfo().getFirstname(),
                object.getPersonalInfo().getLastname(),
                object.getPersonalInfo().getBirthDate(),
                object.getPhone(),
                object.getImage(),
                object.getStatus(),
                object.getRegisteredAt());
    }
}