package com.sspdev.hotelbooking.unit.mapper;

import com.sspdev.hotelbooking.mapper.UserCreateEditMapper;
import com.sspdev.hotelbooking.unit.UnitTestBase;
import com.sspdev.hotelbooking.unit.util.TestDataUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
public class UserCreateEditMapperTest extends UnitTestBase {

    private final UserCreateEditMapper userCreateEditMapper;

    @Test
    void shouldReturnUserFromUserCreateEditDto() {
        var userCreateEditDto = TestDataUtil.getUserCreateEditDto();
        var expectedUser = TestDataUtil.getUser();

        var actualUser = userCreateEditMapper.map(userCreateEditDto);

        assertEquals(expectedUser, actualUser);
    }
}