package com.sspdev.hotelbooking.integration.service;

import com.sspdev.hotelbooking.database.entity.PersonalInfo;
import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.UserReadDto;
import com.sspdev.hotelbooking.dto.filter.UserFilter;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import com.sspdev.hotelbooking.service.UserService;
import com.sspdev.hotelbooking.unit.util.TestDataUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RequiredArgsConstructor
public class UserServiceIT extends IntegrationTestBase {

    private static final Integer ALL_USERS_COLLECTION_SIZE = 5;
    private static final Integer STATUS_ROLE_FILTER_COLLECTION_SIZE = 2;
    private static final Integer FIRST_NAME_FILTER_COLLECTION_SIZE = 1;
    private static final Integer EXISTENT_USER_ID = 1;
    private static final Integer NON_EXISTENT_USER_ID = 999;

    private final UserService userService;

    @Test
    void shouldFindAllUsers() {
        var allUsers = userService.findAll();

        assertEquals(allUsers.size(), ALL_USERS_COLLECTION_SIZE);
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForFindAllByFilter")
    void checkFindAllByFilter(UserFilter filter, Integer expectedNumberOfUsers, String... expectedUsernames) {
        var actualUsers = userService.findAllByFilter(filter);

        var actualUsernames = actualUsers.stream().map(UserReadDto::getUsername).toList();

        assertEquals(expectedNumberOfUsers, actualUsers.size());
        assertThat(actualUsernames).contains(expectedUsernames);
    }

    static Stream<Arguments> getArgumentsForFindAllByFilter() {
        return Stream.of(
//                status-role filter
                Arguments.of(
                        UserFilter.builder().status(Status.NEW).role(Role.USER).build(),
                        STATUS_ROLE_FILTER_COLLECTION_SIZE,
                        new String[]{"FirstUser@gmail.com", "SecondUser@gmail.com"}),
//                with "jack" (lowercase) in username filter
                Arguments.of(UserFilter.builder().firstName("jack").build(),
                        FIRST_NAME_FILTER_COLLECTION_SIZE,
                        new String[]{"SecondOwner@gmail.com"}
                ));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForFindAllWithUserFilterAndPageable")
    void findAll_shouldFindAllUsersAscSorted_withUserFilterAndPageable(UserFilter userFilter, String... expectedFirstNames) {
        var sort = Sort.sort(User.class).by(User::getPersonalInfo).by(PersonalInfo::getFirstname);
        var pageable = PageRequest.of(0, 20, sort);

        var actualUsers = userService.findAll(userFilter, pageable);
        var actualFirstName = actualUsers.stream()
                .map(UserReadDto::getFirstName).toList();

        assertThat(actualFirstName).containsExactly(expectedFirstNames);
    }

    static Stream<Arguments> getArgumentsForFindAllWithUserFilterAndPageable() {
        return Stream.of(
//                no conditional filter
                Arguments.of(UserFilter.builder().build(),
                        new String[]{"Andrey", "Jack", "Michail", "Natalya", "Sergey"}),
//                with "a" in firstname filter
                Arguments.of(UserFilter.builder()
                                .firstName("a")
                                .build(),
                        new String[]{"Andrey", "Jack", "Michail", "Natalya"}),
//                with-User-role filter
                Arguments.of(UserFilter.builder()
                                .role(Role.USER)
                                .build(),
                        new String[]{"Michail", "Natalya"})
        );
    }

    @Test
    void shouldFindUserByUserId() {
        var maybeUser = userService.findById(EXISTENT_USER_ID);

        assertThat(maybeUser).isPresent();
        maybeUser.ifPresent(user -> assertEquals(user.getId(), EXISTENT_USER_ID));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -10, 0, 999, Integer.MAX_VALUE})
    void shouldReturnEmptyWhenFindByFakeId(Integer fakeId) {
        var nonExistentUser = userService.findById(fakeId);

        assertThat(nonExistentUser).isEmpty();
    }

    @Test
    void shouldSaveUserInDatabase() {
        var userToSave = TestDataUtil.getUserCreateEditDto();

        var actualUser = userService.create(userToSave);

        assertAll(() -> {
            assertThat(actualUser.getId()).isPositive();
            assertEquals(actualUser.getUsername(), userToSave.getUsername());
            assertEquals(actualUser.getFirstName(), userToSave.getFirstName());
            assertEquals(actualUser.getLastName(), userToSave.getLastName());
            assertEquals(actualUser.getPhone(), userToSave.getPhone());
            assertEquals(actualUser.getStatus(), Status.NEW);
        });
    }

    @Test
    void shouldUpdateExistentUser() {
        var userCreateEditDto = TestDataUtil.getUserCreateEditDto();

        var maybeExistentUser = userService.findById(EXISTENT_USER_ID);
        var maybeActualUser = userService.update(EXISTENT_USER_ID, userCreateEditDto);

        assertThat(maybeExistentUser).isPresent();
        assertThat(maybeActualUser).isPresent();

        maybeActualUser.ifPresent(updatedUser -> {
            assertEquals(EXISTENT_USER_ID, updatedUser.getId());
            assertEquals(userCreateEditDto.getRole(), updatedUser.getRole());
            assertEquals(userCreateEditDto.getUsername(), updatedUser.getUsername());
            assertEquals(userCreateEditDto.getFirstName(), updatedUser.getFirstName());
            assertEquals(userCreateEditDto.getLastName(), updatedUser.getLastName());
        });
    }

    @ParameterizedTest
    @MethodSource("getDataForDeleteExistentUserTest")
    void checkDelete(Integer userId, boolean expectedResult) {
        var actualResult = userService.delete(userId);

        assertEquals(expectedResult, actualResult);
    }

    static @NotNull Stream<Arguments> getDataForDeleteExistentUserTest() {
        return Stream.of(
                Arguments.of(EXISTENT_USER_ID, true),
                Arguments.of(NON_EXISTENT_USER_ID, false)
        );
    }

    @Test
    void checkChangeStatusByUserId() {
        var expectedStatus = Status.BLOCKED;
        var maybeUser = userService.findById(EXISTENT_USER_ID);

        assertThat(maybeUser).isPresent();

        maybeUser.ifPresent(user -> {
            assertNotEquals(expectedStatus, user.getStatus());
            userService.changeStatus(expectedStatus, user.getId());
            var actualUser = userService.findById(user.getId());
            assertEquals(expectedStatus, actualUser.get().getStatus());
        });
    }
}