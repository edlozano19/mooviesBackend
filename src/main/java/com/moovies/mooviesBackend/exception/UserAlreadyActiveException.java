package com.moovies.mooviesBackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyActiveException extends UserStateException {
    public UserAlreadyActiveException(Long userId) {
        super("User is already active", userId);
    }
}
