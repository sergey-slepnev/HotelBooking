package com.sspdev.hotelbooking.validation.impl;

import com.sspdev.hotelbooking.dto.filter.RoomFilter;
import com.sspdev.hotelbooking.validation.CheckDateFromNotBeforeDateTo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateFromToValidator implements ConstraintValidator<CheckDateFromNotBeforeDateTo, RoomFilter> {

    @Override
    public boolean isValid(RoomFilter filter, ConstraintValidatorContext context) {
        if (filter.checkIn() == null || filter.checkOut() == null) {
            return true;
        }
        return filter.checkIn().isBefore(filter.checkOut());
    }
}