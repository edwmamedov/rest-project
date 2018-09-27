package com.trizic.api.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    static class ErrorResponse {
        private String errorCode;
        ErrorResponse(String errorCode) {
            this.errorCode = errorCode;
        }
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleException(NotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

}
