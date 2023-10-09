package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.PersonalInfo;
import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.UserCreateEditDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.util.function.Predicate.not;

@Component
@RequiredArgsConstructor
public class UserCreateEditMapper implements Mapper<UserCreateEditDto, User> {

    private final PasswordEncoder passwordEncoder;

    @Override
    public User map(UserCreateEditDto object) {
        var user = new User();
        copy(object, user);

        return user;
    }

    @Override
    public User map(UserCreateEditDto fromObject, User toObject) {
        copy(fromObject, toObject);
        return toObject;
    }

    private void copy(UserCreateEditDto object, User user) {
        var personalInfo = new PersonalInfo();

        user.setRole(object.getRole());
        user.setUsername(object.getUsername());
        personalInfo.setFirstname(object.getFirstName());
        personalInfo.setLastname(object.getLastName());
        personalInfo.setBirthDate(object.getBirthDate());
        user.setPersonalInfo(personalInfo);
        user.setPhone(object.getPhone());
        user.setStatus(Status.NEW);
        user.setRegisteredAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        Optional.ofNullable(object.getRawPassword())
                .filter(StringUtils::hasText)
                .map(passwordEncoder::encode)
                .ifPresent(user::setPassword);

        Optional.ofNullable(object.getImage())
                .filter(not(MultipartFile::isEmpty))
                .ifPresent(image -> user.setImage(image.getOriginalFilename()));
    }
}