package com.moovies.mooviesBackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserUsernameAlreadyExists extends UserStateException {
    public UserUsernameAlreadyExists() {
        super("Username already exists", null);

    }
}
