package com.sspdev.hotelbooking.validation;

import com.sspdev.hotelbooking.validation.impl.MaxMinCostValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MaxMinCostValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckCostMaxNotLessCostMin {

    String message() default "{error.max_less_min}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
