package com.moovies.mooviesBackend.exception;

public class UserStateException extends RuntimeException {
    private final Long userId;

    public UserStateException (String message, Long userId) {
        super(message);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
