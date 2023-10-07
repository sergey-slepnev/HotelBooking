package com.sspdev.hotelbooking.unit.util;

import com.sspdev.hotelbooking.database.entity.PersonalInfo;
import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.UserCreateEditDto;
import com.sspdev.hotelbooking.dto.UserReadDto;
import lombok.experimental.UtilityClass;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class TestDataUtil {

    public static User getUser() {
        return User.builder()
                .id(1)
                .role(Role.USER)
                .username("user")
                .password("123")
                .personalInfo(new PersonalInfo("Petr", "Petrov", LocalDate.of(2000, 10, 10)))
                .phone("+7-954-984-98-98")
                .image("user_avatar")
                .status(Status.NEW)
                .registeredAt(LocalDateTime.of(2023, 5, 5, 10, 10))
                .build();
    }

    public static UserReadDto getUserReadDto() {
        return new UserReadDto(
                1,
                Role.USER,
                "user",
                "123",
                "Petr",
                "Petrov",
                LocalDate.of(2000, 10, 10),
                "+7-954-984-98-98",
                "user_avatar",
                Status.NEW,
                LocalDateTime.of(2023, 5, 5, 10, 10));
    }

    public static List<User> getUsers() {
        return List.of(
                getUser(),
                User.builder()
                        .id(2)
                        .role(Role.ADMIN)
                        .username("admin")
                        .password("123")
                        .personalInfo(new PersonalInfo("Ivan", "Ivanov", LocalDate.of(2000, 10, 10)))
                        .phone("+7-954-984-98-99")
                        .image("user_avatar")
                        .status(Status.APPROVED)
                        .registeredAt(LocalDateTime.of(2023, 5, 5, 10, 10))
                        .build(),
                User.builder()
                        .id(3)
                        .role(Role.OWNER)
                        .username("owner")
                        .password("123")
                        .personalInfo(new PersonalInfo("Masha", "Sidorova", LocalDate.of(2000, 10, 10)))
                        .phone("+7-954-984-98-88")
                        .image("user_avatar")
                        .status(Status.NEW)
                        .registeredAt(LocalDateTime.of(2023, 5, 5, 10, 10))
                        .build()
        );
    }

    public static UserCreateEditDto getUserCreateEditDto() {
        return new UserCreateEditDto(
                "user",
                "123",
                "Petr",
                "Petrov",
                LocalDate.of(2000, 10, 10),
                "+7-954-984-98-98",
                Role.USER,
                new MockMultipartFile("userAvatar.jpg", "userAvatar.jpg", "application/octet-stream", new byte[]{})
        );
    }

    public static UserCreateEditDto getUpdatedUserCreateEditDto() {
        return new UserCreateEditDto(
                "updatedUserName",
                "123",
                "Petr",
                "Petrov",
                LocalDate.of(2000, 10, 10),
                "+7-954-984-98-98",
                Role.USER,
                null
        );
    }

    public static List<UserReadDto> getUsersReadDto() {
        return List.of(
                getUserReadDto(),
                new UserReadDto(
                        2,
                        Role.ADMIN,
                        "admin",
                        "123",
                        "Ivan",
                        "Ivanov", LocalDate.of(2000, 10, 10),
                        "+7-954-984-98-99",
                        "user_avatar",
                        Status.APPROVED,
                        LocalDateTime.of(2023, 5, 5, 10, 10)),
                new UserReadDto(
                        2,
                        Role.OWNER,
                        "owner",
                        "123",
                        "Masha",
                        "Sidorova", LocalDate.of(2000, 10, 10),
                        "+7-954-984-98-88",
                        "user_avatar",
                        Status.NEW,
                        LocalDateTime.of(2023, 5, 5, 10, 10))
        );
    }
}