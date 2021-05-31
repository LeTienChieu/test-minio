package com.example.demo.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class ExceptionHandlingController {
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    protected ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ErrorResponse(BAD_REQUEST, ex);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    protected ErrorResponse handleNullPointerException(NullPointerException ex) {
        return new ErrorResponse(BAD_REQUEST, ex);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    protected ErrorResponse handleException(NullPointerException ex) {
        return new ErrorResponse(INTERNAL_SERVER_ERROR, ex);
    }
}
