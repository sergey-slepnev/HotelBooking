package com.sspdev.hotelbooking.unit.mapper;

import com.sspdev.hotelbooking.mapper.UserReadMapper;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import com.sspdev.hotelbooking.unit.util.TestDataUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
class UserReadMapperTest extends UnitTestBase {

    private final UserReadMapper userReadMapper;

    @Test
    void shouldReturnUserReadDtoFromUser() {
        var userEntity = TestDataUtil.getUser();
        var expectedDto = TestDataUtil.getUserReadDto();

        var actualDto = userReadMapper.map(userEntity);

        assertEquals(expectedDto, actualDto);
    }
}