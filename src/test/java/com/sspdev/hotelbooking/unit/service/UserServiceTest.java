package com.sspdev.hotelbooking.unit.service;

import com.sspdev.hotelbooking.database.entity.PersonalInfo;
import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.database.repository.UserRepository;
import com.sspdev.hotelbooking.dto.UserReadDto;
import com.sspdev.hotelbooking.mapper.UserCreateEditMapper;
import com.sspdev.hotelbooking.mapper.UserReadMapper;
import com.sspdev.hotelbooking.service.UserService;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import com.sspdev.hotelbooking.unit.util.TestDataUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RequiredArgsConstructor
public class UserServiceTest extends UnitTestBase {

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
}