package com.sspdev.hotelbooking.integration.service;

import com.sspdev.hotelbooking.database.entity.enums.Status;
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

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RequiredArgsConstructor
public class UserServiceIT extends IntegrationTestBase {

    private static final Integer ALL_USERS_COLLECTION_SIZE = 5;
    private static final Integer EXISTENT_USER_ID = 1;
    private static final Integer NON_EXISTENT_USER_ID = 999;

    private final UserService userService;

    @Test
    void shouldFindAllUsers() {
        var allUsers = userService.findAll();

        assertEquals(allUsers.size(), ALL_USERS_COLLECTION_SIZE);
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

        maybeActualUser.ifPresent(user -> {
            assertEquals(EXISTENT_USER_ID, user.getId());
            assertEquals(userCreateEditDto.getRole(), user.getRole());
            assertEquals(userCreateEditDto.getUsername(), user.getUsername());
            assertEquals(userCreateEditDto.getFirstName(), user.getFirstName());
            assertEquals(userCreateEditDto.getLastName(), user.getLastName());
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