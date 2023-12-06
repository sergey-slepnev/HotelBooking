package com.sspdev.hotelbooking.unit.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.PageResponse;
import com.sspdev.hotelbooking.dto.UserReadDto;
import com.sspdev.hotelbooking.dto.filter.UserFilter;
import com.sspdev.hotelbooking.http.controller.UserController;
import com.sspdev.hotelbooking.service.BookingRequestService;
import com.sspdev.hotelbooking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final Integer EXISTENT_USER_ID = 1;
    private static final Integer NON_EXISTENT_USER_ID = 999;

    private MockMvc mockMvc;
    @Mock
    private Page<UserReadDto> userReadDtoPage;
    @Mock
    private UserService userService;
    @Mock
    private BookingRequestService bookingRequestService;
    @InjectMocks
    public UserController userController;

    @BeforeEach
    public void initMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .alwaysDo(print())
                .build();
    }

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
        when(bookingRequestService.countRequestsByUserAndStatus(EXISTENT_USER_ID)).thenReturn(Map.of());
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
    void update_shouldReturnUpdatePage_whenUserInSession() throws Exception {
        mockMvc.perform(get("/my-booking/users/" + EXISTENT_USER_ID + "/update")
                        .sessionAttr("user", getUserReadDto()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("user/update"));
    }

    @Test
    void update_shouldRedirectToUpdatePage_whenUpdateDtoInvalid() throws Exception {
        mockMvc.perform(post("/my-booking/users/" + EXISTENT_USER_ID + "/update")
                        .with(csrf())
                        .param(username, "N")
                        .param(firstName, "A")
                        .param(lastName, "S")
                        .param(phone, "8")
                        .param(rawPassword, "testPassword")
                        .param(role, Role.USER.name())
                        .param(birthDate, "1988-10-10"))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrlPattern("/my-booking/users/{\\d+}/update"),
                        flash().attributeCount(2),
                        flash().attributeExists("user", "errors"),
                        flash().attribute("errors", hasSize(2))
                );
    }

    @Test
    void delete_shouldDeleteUser_whenUserExists() throws Exception {
        when(userService.delete(EXISTENT_USER_ID)).thenReturn(true);

        mockMvc.perform(post("/my-booking/users/" + EXISTENT_USER_ID + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-booking/users"));
    }

    @Test
    void delete_shouldReturn404_whenUserNotExist() throws Exception {
        when(userService.delete(NON_EXISTENT_USER_ID)).thenReturn(false);

        mockMvc.perform(post("/my-booking/users/" + NON_EXISTENT_USER_ID + "/delete"))
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