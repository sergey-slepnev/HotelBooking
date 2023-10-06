package com.sspdev.hotelbooking.unit.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.PageResponse;
import com.sspdev.hotelbooking.dto.UserReadDto;
import com.sspdev.hotelbooking.dto.filter.UserFilter;
import com.sspdev.hotelbooking.http.controller.UserController;
import com.sspdev.hotelbooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RequiredArgsConstructor
@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final Integer EXISTENT_USER_ID = 1;
    private static final Integer NON_EXISTENT_USER_ID = 999;

    private final MockMvc mockMvc;

    @MockBean
    private final Page<UserReadDto> userReadDtoPage;

    @MockBean
    private final UserService userService;

    @ParameterizedTest
    @ValueSource(longs = {0L, 5L, 10L, 100L})
    void findAll_shouldFindAllUsers(long expectedTotalElementsInPage) throws Exception {
        var pageMetadata = new PageResponse.Metadata(0, 20, expectedTotalElementsInPage);
        var expectedResponseContent = List.of(getUserReadDto());
        var pageResponse = new PageResponse<>(expectedResponseContent, pageMetadata);

        when(userReadDtoPage.getContent()).thenReturn(expectedResponseContent);
        when(userReadDtoPage.getSize()).thenReturn(20);
        when(userReadDtoPage.getTotalElements()).thenReturn(expectedTotalElementsInPage);
        when(userService.findAll(any(UserFilter.class), any(Pageable.class))).thenReturn(userReadDtoPage);

        var mvcResult = mockMvc.perform(get("/my-booking/users"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("users", "roles", "filter", "statuses"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(model().attribute("users", pageResponse))
                .andExpect(model().attribute("roles", Role.values()))
                .andExpect(model().attribute("statuses", Status.values()))
                .andExpect(model().attribute("filter", new UserFilter(null, null, null, null)))
                .andReturn();

        var string = requireNonNull(mvcResult.getModelAndView()).getModel().get("users").toString();

        assertThat(string).contains("totalElements=" + expectedTotalElementsInPage);
    }

    @Test
    void findById_shouldFindUserById_whenUserExists() throws Exception {
        var expectedUserReadDto = getUserReadDto();
        when(userService.findById(anyInt())).thenReturn(Optional.of(expectedUserReadDto));

        mockMvc.perform(get("/my-booking/users/" + EXISTENT_USER_ID))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("user", expectedUserReadDto));
    }

    @Test
    void findById_shouldReturnNotFound_whenUserNotExist() throws Exception {
        when(userService.findById(NON_EXISTENT_USER_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/my-booking/users/" + NON_EXISTENT_USER_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void registration_shouldReturnRegistrationPage() throws Exception {
        mockMvc.perform(get("/my-booking/users/registration"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("user/registration"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void update_shouldReturnUpdatePage_whenUserInSession() throws Exception {
        mockMvc.perform(get("/my-booking/users/" + EXISTENT_USER_ID + "/update")
                        .sessionAttr("user", getUserReadDto()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("user/update"));

    }

    private UserReadDto getUserReadDto() {
        return new UserReadDto(
                EXISTENT_USER_ID,
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
}