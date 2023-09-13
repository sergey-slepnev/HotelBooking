package com.sspdev.hotelbooking.dto;

import com.sspdev.hotelbooking.database.entity.enums.Role;
import lombok.Value;
import lombok.experimental.FieldNameConstants;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Value
@FieldNameConstants
public class UserCreateEditDto {

    String username;

    String rawPassword;

    String firstName;

    String lastName;

    LocalDate birthDate;

    String phone;

    Role role;

    MultipartFile image;
}