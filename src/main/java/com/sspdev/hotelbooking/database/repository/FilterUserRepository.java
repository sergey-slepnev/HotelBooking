package com.sspdev.hotelbooking.database.repository;

import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.dto.filter.UserFilter;

import java.util.List;

public interface FilterUserRepository {

    List<User> findAllByFilter(UserFilter filter);
}