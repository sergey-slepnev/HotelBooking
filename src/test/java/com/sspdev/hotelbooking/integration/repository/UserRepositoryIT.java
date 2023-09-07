package com.sspdev.hotelbooking.integration.repository;

import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.database.repository.UserRepository;
import com.sspdev.hotelbooking.dto.filter.UserFilter;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Map.entry;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
public class UserRepositoryIT extends IntegrationTestBase {

    private static final Integer NO_PREDICATE_FILTER_PAGE_SIZE = 5;
    private static final Integer ROLE_STATUS_FILTER_PAGE_SIZE = 2;

    private final UserRepository userRepository;

    @ParameterizedTest
    @MethodSource("getArgumentsForFindByUserNameMethod")
    void shouldFindExistingUserByUsername(String username, String firstName, String lastName, String registrationDate) {
        var maybeUser = userRepository.findByUsername(username);

        assertTrue(maybeUser.isPresent());
        maybeUser.ifPresent(user -> assertAll(() -> {
            assertEquals(user.getUsername(), username);
            assertEquals(user.getPersonalInfo().getFirstname(), firstName);
            assertEquals(user.getPersonalInfo().getLastname(), lastName);
            assertEquals(user.getRegisteredAt().toString().replace('T', ' '), registrationDate);
        }));
    }

    static Stream<Arguments> getArgumentsForFindByUserNameMethod() {
        return Stream.of(
                Arguments.of("AdminEmail@gmail.com", "Sergey", "Sidorov", "2022-09-08 10:00"),
                Arguments.of("FirstUser@gmail.com", "Natalya", "Stepanova", "2022-10-12 11:22"),
                Arguments.of("SecondUser@gmail.com", "Michail", "Malyshev", "2022-10-21 13:10"),
                Arguments.of("FirstOwner@gmail.com", "Andrey", "Petrov", "2022-11-18 10:00"),
                Arguments.of("SecondOwner@gmail.com", "Jack", "Ivanov", "2022-11-15 11:00")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "dummyUser")
    void shouldNotFindUserByIncorrectUsername(String username) {
        var nonexistentUser = userRepository.findByUsername(username);

        assertFalse(nonexistentUser.isPresent());
    }

    @Test
    void shouldChangeUserStatus() {
        var newUser = userRepository.findByUsername("FirstUser@gmail.com");
        newUser.ifPresent(user -> assertEquals(user.getStatus(), Status.NEW));

        userRepository.changeStatusByUserId(Status.APPROVED, newUser.get().getId());

        var approvedUser = userRepository.findByUsername("FirstUser@gmail.com");
        assertEquals(approvedUser.get().getStatus(), Status.APPROVED);
    }

    @ParameterizedTest
    @MethodSource("getUserFilterUserNameAndMapOfRegistrationDates")
    void findUsersByFilter(UserFilter filter, int userCollectionSize, Map<String, String> usernameRegisteredAtDateMap) {
        var usersByFilter = userRepository.findAllByFilter(filter);

        assertEquals(usersByFilter.size(), userCollectionSize);

        var userRegistrationDates = usersByFilter.stream()
                .collect(toMap(
                        User::getUsername,
                        user -> user.getRegisteredAt().toLocalDate().toString(),
                        (previousEmail, newEmail) -> newEmail, LinkedHashMap::new));
        usernameRegisteredAtDateMap.forEach((key, value) -> assertThat(userRegistrationDates).contains(entry(key, value)));
    }

    static Stream<Arguments> getUserFilterUserNameAndMapOfRegistrationDates() {
        return Stream.of(
//                no predicate filter
                Arguments.of(UserFilter.builder().build(),
                        NO_PREDICATE_FILTER_PAGE_SIZE,
                        Map.of(
                                "FirstOwner@gmail.com", "2022-11-18",
                                "SecondOwner@gmail.com", "2022-11-15",
                                "SecondUser@gmail.com", "2022-10-21",
                                "FirstUser@gmail.com", "2022-10-12",
                                "AdminEmail@gmail.com", "2022-09-08")),
//                role-status filter
                Arguments.of(UserFilter.builder()
                                .role(Role.OWNER)
                                .status(Status.NEW)
                                .build(),
                        ROLE_STATUS_FILTER_PAGE_SIZE,
                        Map.of(
                                "FirstOwner@gmail.com", "2022-11-18",
                                "SecondOwner@gmail.com", "2022-11-15"
                        ))
        );
    }
}