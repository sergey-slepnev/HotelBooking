package com.sspdev.hotelbooking.integration.service;

import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import com.sspdev.hotelbooking.service.UserService;
import com.sspdev.hotelbooking.unit.util.TestDataUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
public class UserServiceIT extends IntegrationTestBase {

    private static final Integer ALL_USERS_COLLECTION_SIZE = 5;
    private static final Integer EXISTENT_USER_ID = 1;

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
}