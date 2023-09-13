package com.sspdev.hotelbooking.unit.annotation;

import com.sspdev.hotelbooking.TestApplicationRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = TestApplicationRunner.class)
public @interface UT {

}