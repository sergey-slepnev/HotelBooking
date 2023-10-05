package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.PersonalInfo;
import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.UserCreateEditDto;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.util.function.Predicate.not;

@Component
public class UserCreateEditMapper implements Mapper<UserCreateEditDto, User> {

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

    private static void copy(UserCreateEditDto object, User user) {
        var personalInfo = new PersonalInfo();

        user.setRole(object.getRole());
        user.setUsername(object.getUsername());
        user.setPassword(object.getRawPassword());
        personalInfo.setFirstname(object.getFirstName());
        personalInfo.setLastname(object.getLastName());
        personalInfo.setBirthDate(object.getBirthDate());
        user.setPersonalInfo(personalInfo);
        user.setPhone(object.getPhone());
        user.setStatus(Status.NEW);
        user.setRegisteredAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        Optional.ofNullable(object.getImage())
                .filter(not(MultipartFile::isEmpty))
                .ifPresent(image -> user.setImage(image.getOriginalFilename()));
    }
}