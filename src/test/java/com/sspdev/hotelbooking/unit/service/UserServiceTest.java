package com.sspdev.hotelbooking.unit.service;

import com.sspdev.hotelbooking.database.entity.PersonalInfo;
import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.database.repository.UserRepository;
import com.sspdev.hotelbooking.dto.UserReadDto;
import com.sspdev.hotelbooking.dto.filter.UserFilter;
import com.sspdev.hotelbooking.mapper.UserCreateEditMapper;
import com.sspdev.hotelbooking.mapper.UserReadMapper;
import com.sspdev.hotelbooking.service.UserService;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import com.sspdev.hotelbooking.unit.util.TestDataUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RequiredArgsConstructor
public class UserServiceTest extends UnitTestBase {

    private static final Integer ROLE_FILTER_COLLECTION_SIZE = 1;
    private static final Integer FIRST_NAME_FILTER_COLLECTION_SIZE = 0;

    @MockBean
    private final UserRepository userRepository;
    @MockBean
    private final UserReadMapper userReadMapper;
    @MockBean
    private final UserCreateEditMapper userCreateEditMapper;
    @InjectMocks
    private final UserService userService;

    @Test
    void shouldFindAll() {
        var users = TestDataUtil.getUsers();
        var expectedResult = TestDataUtil.getUsersReadDto();

        doReturn(users).when(userRepository).findAll();

        var actualResult = userService.findAll();
        verify(userReadMapper, times(3)).map(any());

        assertNotNull(actualResult);
        assertThat(actualResult.size()).isEqualTo(expectedResult.size());
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForFindAllByFilter")
    void checkFindAllByFilter(UserFilter filter, Integer expectedNumberOfUsers, List<User> expectedUsers) {
        doReturn(expectedUsers).when(userRepository).findAllByFilter(filter);

        var actualUsers = userService.findAllByFilter(filter);

        assertEquals(expectedNumberOfUsers, actualUsers.size());
        verify(userReadMapper, times(expectedNumberOfUsers)).map(any());
    }

    static Stream<Arguments> provideArgumentsForFindAllByFilter() {
        return Stream.of(
//                role filter -> one user
                Arguments.of(
                        UserFilter.builder().role(Role.ADMIN).build(),
                        ROLE_FILTER_COLLECTION_SIZE,
                        List.of(User.builder()
                                .id(1)
                                .role(Role.ADMIN)
                                .username("AdminEmail@gmail.com")
                                .password("AdminPassword")
                                .personalInfo(new PersonalInfo("Sergey", "Sidorov", LocalDate.of(1984, 4, 22)))
                                .phone("8-835-66-99-333")
                                .image("AdminAvatar.jpg")
                                .status(Status.APPROVED)
                                .registeredAt(LocalDateTime.of(2022, 9, 21, 13, 10))
                                .build())),
//                with "dummy" in username filter -> zero users
                Arguments.of(UserFilter.builder().firstName("dummy").build(),
                        FIRST_NAME_FILTER_COLLECTION_SIZE,
                        List.of()
                ));
    }

    @Test
    void shouldFindById() {
        var user = TestDataUtil.getUser();
        var expectedDto = TestDataUtil.getUserReadDto();

        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(expectedDto).when(userReadMapper).map(user);

        var actualResult = userService.findById(user.getId());

        assertThat(actualResult).isPresent();

        actualResult.ifPresent(result -> assertEquals(expectedDto, result));
    }

    @Test
    void shouldSaveUserInDataBase() {
        var createDto = TestDataUtil.getUserCreateEditDto();
        var userReadDto = TestDataUtil.getUserReadDto();
        var user = TestDataUtil.getUser();

        doReturn(user).when(userCreateEditMapper).map(createDto);
        doReturn(user).when(userRepository).save(user);
        doReturn(userReadDto).when(userReadMapper).map(user);

        var actualUserReadDto = userService.create(createDto);

        assertThat(actualUserReadDto.getId()).isNotNull();
    }

    @Test
    void shouldUpdateExistentUser() {
        var user = TestDataUtil.getUser();
        var updatedDto = TestDataUtil.getUpdatedUserCreateEditDto();

        var updatedUser = new User(
                user.getId(),
                updatedDto.getRole(),
                updatedDto.getUsername(),
                updatedDto.getRawPassword(),
                new PersonalInfo(
                        updatedDto.getFirstName(),
                        updatedDto.getLastName(),
                        updatedDto.getBirthDate()
                ),
                updatedDto.getPhone(),
                null,
                Status.NEW,
                LocalDateTime.now(),
                null,
                null,
                null);
        var updatedReadDto = new UserReadDto(
                user.getId(),
                user.getRole(),
                user.getUsername(),
                user.getPassword(),
                user.getPersonalInfo().getFirstname(),
                user.getPersonalInfo().getLastname(),
                user.getPersonalInfo().getBirthDate(),
                user.getPhone(),
                user.getImage(),
                user.getStatus(),
                user.getRegisteredAt()
        );

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userCreateEditMapper.map(updatedDto, user)).thenReturn(updatedUser);
        when(userRepository.saveAndFlush(updatedUser)).thenReturn(user);
        when(userReadMapper.map(user)).thenReturn(updatedReadDto);

        var actualUser = userService.update(user.getId(), updatedDto);
        assertThat(actualUser).isPresent();
    }

    @Test
    void checkDelete() {
        var user = TestDataUtil.getUser();

        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doNothing().when(userRepository).delete(user);
        doNothing().when(userRepository).flush();

        var actualResul = userService.delete(user.getId());

        assertTrue(actualResul);
    }
}