package com.sspdev.hotelbooking.validation;

import com.sspdev.hotelbooking.validation.impl.ContentTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ContentTypeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckContentType {

    String message() default "{error.invalid_content_type}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}