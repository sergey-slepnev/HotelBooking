package com.sspdev.hotelbooking.database.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.dto.filter.UserFilter;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.sspdev.hotelbooking.database.entity.QUser.user;

@RequiredArgsConstructor
public class FilterUserRepositoryImpl implements FilterUserRepository {

    private final EntityManager entityManager;

    @Override
    public List<User> findAllByFilter(UserFilter filter) {
        var predicate = com.sspdev.hotelrepository.database.querydsl.QPredicates.builder()
                .add(filter.role(), user.role::eq)
                .add(filter.firstName(), user.personalInfo.firstname::containsIgnoreCase)
                .add(filter.lastName(), user.personalInfo.lastname::containsIgnoreCase)
                .add(filter.status(), user.status::eq)
                .build();

        return new JPAQuery<User>(entityManager)
                .select(user)
                .from(user)
                .where(predicate)
                .orderBy(user.registeredAt.desc())
                .fetch();
    }
}