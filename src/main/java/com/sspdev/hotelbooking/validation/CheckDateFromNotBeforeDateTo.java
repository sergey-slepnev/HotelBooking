package com.sspdev.hotelbooking.validation;

import com.nimbusds.jose.Payload;
import com.sspdev.hotelbooking.validation.impl.DateFromToValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateFromToValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckDateFromNotBeforeDateTo {

    String message() default "{error.date_from_to}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}