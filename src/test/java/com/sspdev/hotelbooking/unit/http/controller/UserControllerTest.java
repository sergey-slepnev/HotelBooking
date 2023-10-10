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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RequiredArgsConstructor
@WebMvcTest(UserController.class)
@WithMockUser(username = "test@gmail.com", password = "test", authorities = {"ADMIN", "USER", "OWNER"})
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
                .andExpect(status().isNotFound())
                .andExpect(mvcResult ->
                        requireNonNull(mvcResult.getResolvedException()).getClass()
                                .equals(ResponseStatusException.class));
    }

    @Test
    void registration_shouldReturnRegistrationPage() throws Exception {
        mockMvc.perform(get("/my-booking/registration"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("user/registration"))
                .andExpect(model().attributeExists("userCreateDto"));
    }

    @Test
    void update_shouldReturnUpdatePage_whenUserInSession() throws Exception {
        mockMvc.perform(get("/my-booking/users/" + EXISTENT_USER_ID + "/update")
                        .sessionAttr("user", getUserReadDto()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("user/update"));
    }

    @Test
    void delete_shouldDeleteUser_whenUserExists() throws Exception {
        when(userService.delete(EXISTENT_USER_ID)).thenReturn(true);

        mockMvc.perform(post("/my-booking/users/"  + EXISTENT_USER_ID + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-booking/users"));
    }

    @Test
    void delete_shouldReturn404_whenUserNotExist() throws Exception {
        when(userService.delete(NON_EXISTENT_USER_ID)).thenReturn(false);

        mockMvc.perform(post("/my-booking/users/"  + NON_EXISTENT_USER_ID + "/delete"))
                .andExpect(status().is4xxClientError());
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