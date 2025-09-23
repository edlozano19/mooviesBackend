package com.moovies.mooviesBackend.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserEmailAlreadyExistsException extends UserStateException {
    public UserEmailAlreadyExistsException () {
        super("Email already exists", null);
    }
}
