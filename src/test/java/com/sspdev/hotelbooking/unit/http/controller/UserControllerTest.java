package com.sspdev.hotelbooking.unit.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.PageResponse;
import com.sspdev.hotelbooking.dto.UserReadDto;
import com.sspdev.hotelbooking.dto.filter.UserFilter;
import com.sspdev.hotelbooking.http.controller.UserController;
import com.sspdev.hotelbooking.service.UserService;
import lombok.RequiredArgsConstructor;
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

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@WebMvcTest(UserController.class)
class UserControllerTest {

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
                .andExpect(status().isOk())
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

    private UserReadDto getUserReadDto() {
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
}