package com.moovies.mooviesBackend.exception;

public class UserPasswordMismatchException extends UserStateException {
    public UserPasswordMismatchException() {
        super("Current password is incorrect", null);
    }
}
