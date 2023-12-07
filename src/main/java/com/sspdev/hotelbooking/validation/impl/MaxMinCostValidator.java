package com.sspdev.hotelbooking.validation.impl;

import com.sspdev.hotelbooking.dto.filter.RoomFilter;
import com.sspdev.hotelbooking.validation.CheckCostMaxNotLessCostMin;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxMinCostValidator implements ConstraintValidator<CheckCostMaxNotLessCostMin, RoomFilter> {

    @Override
    public boolean isValid(RoomFilter filter, ConstraintValidatorContext context) {
        if (filter.costFrom() == null || filter.costTo() == null) {
            return true;
        }
        return filter.costTo().compareTo(filter.costFrom()) >= 0;
    }
}