package com.moovies.mooviesBackend.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.moovies.mooviesBackend.exception.UserAlreadyActiveException;
import com.moovies.mooviesBackend.exception.UserAlreadyInactiveException;
import com.moovies.mooviesBackend.exception.UserNotFoundException;
import com.moovies.mooviesBackend.user.controller.UserController.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyActiveException.class) 
    public ResponseEntity<ErrorResponse> handleUserAlreadyActiveException(UserAlreadyActiveException ex) {
        logger.warn("User already active: userId={}", ex.getUserId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(ex.getMessage(), ex.getUserId()));
    }

    @ExceptionHandler(UserAlreadyInactiveException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyInactiveException(UserAlreadyInactiveException ex) {
        logger.warn("User already inactive: userId={}", ex.getUserId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(ex.getMessage(), ex.getUserId()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        logger.warn("User not found: userId={}", ex.getUserId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage(), ex.getUserId()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        logger.error("Unexpected runtime exception: {}", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("Internal server error"));
    }
    
}
