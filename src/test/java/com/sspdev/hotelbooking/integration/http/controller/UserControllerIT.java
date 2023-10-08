package com.sspdev.hotelbooking.integration.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.UserReadDto;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import com.sspdev.hotelbooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static com.sspdev.hotelbooking.dto.UserCreateEditDto.Fields.birthDate;
import static com.sspdev.hotelbooking.dto.UserCreateEditDto.Fields.firstName;
import static com.sspdev.hotelbooking.dto.UserCreateEditDto.Fields.lastName;
import static com.sspdev.hotelbooking.dto.UserCreateEditDto.Fields.phone;
import static com.sspdev.hotelbooking.dto.UserCreateEditDto.Fields.rawPassword;
import static com.sspdev.hotelbooking.dto.UserCreateEditDto.Fields.role;
import static com.sspdev.hotelbooking.dto.UserCreateEditDto.Fields.username;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RequiredArgsConstructor
@AutoConfigureMockMvc
class UserControllerIT extends IntegrationTestBase {

    private static final Integer EXISTENT_USER_ID = 1;
    private static final Integer NON_EXISTENT_USER_ID = 999;
    private static final Integer TOTAL_NUMBER_OF_USERS = 5;
    private static final Integer NUMBER_OF_ADMINS = 1;
    private static final Integer NUMBER_OF_OWNERS = 2;
    private static final Integer NUMBER_OF_USERS = 2;

    private final MockMvc mockMvc;
    private final UserService userService;

    @ParameterizedTest
    @MethodSource("getArgumentsForFindAllWithFilter")
    void findAll_shouldFindAllUsers_whenUsersFilteredByRole(String paramName, String paramValue, Integer expectedNumberOfUsers) throws Exception {
        var mvcMethodResult = mockMvc.perform(get("/my-booking/users").queryParam(paramName, paramValue))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("user/users"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("users", "filter", "roles", "statuses"))
                .andExpect(model().attribute("roles", Role.values()))
                .andExpect(model().attribute("statuses", Status.values()))
                .andReturn();

        var resultInString = requireNonNull(mvcMethodResult.getModelAndView()).getModel().get("users").toString();

        assertThat(resultInString).contains("totalElements=" + expectedNumberOfUsers);
    }

    static Stream<Arguments> getArgumentsForFindAllWithFilter() {
        return Stream.of(
                Arguments.of("role", "",
                        TOTAL_NUMBER_OF_USERS),
                Arguments.of(UserReadDto.Fields.role, "ADMIN",
                        NUMBER_OF_ADMINS),
                Arguments.of(UserReadDto.Fields.role, "OWNER",
                        NUMBER_OF_OWNERS),
                Arguments.of(UserReadDto.Fields.role, "USER",
                        NUMBER_OF_USERS)
        );
    }

    @Test
    void findById_shouldFindUserById_whenUserExists() throws Exception {
        mockMvc.perform(get("/my-booking/users/" + EXISTENT_USER_ID))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("user/user"))
                .andExpect(model().attribute("user", getUserReadDto()));
    }

    @Test
    void findById_shouldReturnNotFound_whenUserNotExist() throws Exception {
        mockMvc.perform(get("/my-booking/users/" + NON_EXISTENT_USER_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldCreateNewUser_whenUserValid() throws Exception {
        mockMvc.perform(post("/my-booking/users/create")
                        .param(username, "Test@gmail.com")
                        .param(firstName, "Anton")
                        .param(lastName, "Sidorov")
                        .param(phone, "8-888-88-88-88")
                        .param(rawPassword, "testPassword")
                        .param(role, Role.USER.name())
                        .param(birthDate, "1988-10-10"))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/my-booking/users"),
                        flash().attributeCount(0)
                );
    }

    @Test
    void create_shouldRedirectToRegistrationPage_whenUserInvalid() throws Exception {
        mockMvc.perform(post("/my-booking/users/create")
                        .param(username, "TestUsername")
                        .param(firstName, "Me")
                        .param(lastName, "My")
                        .param(phone, "22")
                        .param(rawPassword, "12")
                        .param(role, Role.USER.name())
                        .param(birthDate, "1988-10-10"))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/my-booking/users/registration"),
                        flash().attributeCount(2),
                        flash().attributeExists("userCreateDto", "errors"),
                        flash().attribute("errors", hasSize(5)));
    }

    @Test
    void update_shouldReturnUserUpdatePage_whenUserInSession() throws Exception {
        var existentUser = userService.findById(EXISTENT_USER_ID);

        mockMvc.perform(get("/my-booking/users/" + EXISTENT_USER_ID + "/update")
                        .sessionAttr("user", existentUser.get()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("user/update"))
                .andExpect(model().attribute("user", existentUser.get()));
    }

    @Test
    void update_shouldReturnClientError_whenThereIsNoUserInSession() throws Exception {
        mockMvc.perform(get("/my-booking/users/" + EXISTENT_USER_ID + "/update"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void delete_shouldDeleteUser_whenUserExists() throws Exception {
        mockMvc.perform(post("/my-booking/users/" + EXISTENT_USER_ID + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-booking/users"));
    }

    @Test
    void delete_shouldReturn404_whenUserNotExist() throws Exception {
        mockMvc.perform(post("/my-booking/users/" + NON_EXISTENT_USER_ID + "/delete"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void changeStatus_shouldChangeStatus_whenUserExists() throws Exception {
        mockMvc.perform(post("/my-booking/users/" + EXISTENT_USER_ID + "/change-status")
                        .param("status", Status.BLOCKED.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-booking/users/" + EXISTENT_USER_ID));
    }

    private UserReadDto getUserReadDto() {
        return new UserReadDto(
                EXISTENT_USER_ID,
                Role.ADMIN,
                "AdminEmail@gmail.com",
                "AdminPassword",
                "Sergey",
                "Sidorov",
                LocalDate.of(1985, 4, 22),
                "8-835-66-99-333",
                "AdminAvatar.jpg",
                Status.APPROVED,
                LocalDateTime.of(2022, 9, 8, 10, 0));
    }
}