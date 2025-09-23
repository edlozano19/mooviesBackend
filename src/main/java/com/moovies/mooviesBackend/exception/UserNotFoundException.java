package com.moovies.mooviesBackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends UserStateException {
    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId, userId);
    }

    public UserNotFoundException(String searchCriteria) {
        super("User not found with " + searchCriteria, null);
    }
}
