package com.sspdev.hotelbooking.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class ControllerAspect {

    @Pointcut("execution(public * com.sspdev.hotelbooking.http.controller.*Controller.*(..))")
    public void isAnyControllerMethod() {
    }

    @Around("isAnyControllerMethod()")
    public Object logAroundControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        var joinPoints = Arrays.toString(joinPoint.getArgs());
        log.info("Enter: {}.{}() with argument[s]: {}.", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), joinPoints);
        var clock = new StopWatch(joinPoint.toString());
        try {
            clock.start();
            var result = joinPoint.proceed();
            clock.stop();
            log.info("Exit: {}.{}() with result: {}. Execution time: {}ms.", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), result, clock.getTotalTimeMillis());
            return result;
        } catch (Throwable ex) {
            log.error("Exception in {}.{}() with cause: {}.", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), ex.toString());
            throw ex;
        }
    }
}