package com.sspdev.hotelbooking.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class ServiceAspect {

    @Pointcut("execution(public * com.sspdev.hotelbooking.service.*Service.*(..))")
    public void isAndyPublicServiceMethod() {
    }

    @Around("isAndyPublicServiceMethod()")
    public Object logAroundServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        var joinPoints = Arrays.toString(joinPoint.getArgs());
        log.info("Enter: {}.{}() with argument[s]: {}.", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), joinPoints);
        try {
            var startTime = System.currentTimeMillis();
            var result = joinPoint.proceed();
            var takenTimeInMilliseconds = System.currentTimeMillis() - startTime;
            log.info("Exit: {}.{}() with result: {}. Execution time: {}ms.", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), result.toString(), takenTimeInMilliseconds);
            return result;
        } catch (Throwable ex) {
            log.error("Exception in {}.{}() with cause: {}.", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), ex.toString());
            throw ex;
        }
    }
}