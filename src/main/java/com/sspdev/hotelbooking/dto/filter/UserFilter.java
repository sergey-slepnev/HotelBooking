package com.sspdev.hotelbooking.dto.filter;

import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import lombok.Builder;

@Builder
public record UserFilter(Role role,
                         String firstName,
                         String lastName,
                         Status status) {

}
