package com.moovies.mooviesBackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyInactiveException extends UserStateException {
    public UserAlreadyInactiveException(Long userId) {
        super("User is already inactive", userId);
    }
}
