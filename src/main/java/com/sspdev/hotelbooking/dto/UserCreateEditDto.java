package com.sspdev.hotelbooking.dto;

import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.validation.group.CreateAction;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Value;
import lombok.experimental.FieldNameConstants;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Value
@FieldNameConstants
public class UserCreateEditDto {

    @Email(message = "{error.email}")
    @Size(min = 1, max = 128, message = "{error.length.username}")
    String username;

    @NotBlank(message = "{error.blank.password}", groups = CreateAction.class)
    @Size(min = 3, max = 128, message = "{error.length.password}", groups = CreateAction.class)
    String rawPassword;

    @NotBlank(message = "{error.blank.firstname}")
    @Size(min = 3, max = 128, message = "{error.length.firstname}")
    String firstName;

    @NotBlank(message = "{error.blank.lastname}")
    @Size(min = 3, max = 128, message = "{error.length.lastname}")
    String lastName;

    @Past(message = "{error.birthdate}")
    LocalDate birthDate;

    @NotBlank(message = "{error.blank.phone}")
    @Size(min = 3, max = 128, message = "{error.length.phone}")
    String phone;

    @NotNull(message = "{error.role.null}")
    Role role;

    MultipartFile image;
}