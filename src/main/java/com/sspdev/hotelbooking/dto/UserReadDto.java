package com.sspdev.hotelbooking.dto;

import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@FieldNameConstants
public class UserReadDto {

    Integer id;
    Role role;
    String username;
    String password;
    String firstName;
    String lastName;
    LocalDate birthdate;
    String phone;
    String image;
    Status status;
    LocalDateTime registeredAt;
}