package com.sspdev.hotelbooking.http.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class ControllerExceptionHandler {

    private static final String NOT_FOUND_STATUS = "404 NOT_FOUND";

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(Exception.class)
    public String handleExceptions(Exception exception) {
        log.error(exception.getCause() + exception.getMessage());
        if (exception instanceof ResponseStatusException && exception.getMessage().equals(NOT_FOUND_STATUS)) {
            return "error/404";
        } else {
            return "error/unexpected_error";
        }
    }
}