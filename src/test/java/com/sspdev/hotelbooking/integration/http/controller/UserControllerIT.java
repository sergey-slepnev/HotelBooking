package com.sspdev.hotelbooking.integration.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static com.sspdev.hotelbooking.dto.UserReadDto.Fields.role;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RequiredArgsConstructor
@AutoConfigureMockMvc
class UserControllerIT extends IntegrationTestBase {

    private static final Integer TOTAL_NUMBER_OF_USERS = 5;
    private static final Integer NUMBER_OF_ADMINS = 1;
    private static final Integer NUMBER_OF_OWNERS = 2;
    private static final Integer NUMBER_OF_USERS = 2;

    private final MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("getArgumentsForFindAllWithFilter")
    void findAll_shouldFindAllUsers_whenUsersFilteredByRole(String paramName, String paramValue, Integer expectedNumberOfUsers) throws Exception {
        var mvcMethodResult = mockMvc.perform(get("/my-booking/users").param(paramName, paramValue))
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
                Arguments.of(role, "ADMIN",
                        NUMBER_OF_ADMINS),
                Arguments.of(role, "OWNER",
                        NUMBER_OF_OWNERS),
                Arguments.of(role, "USER",
                        NUMBER_OF_USERS)
        );
    }
}