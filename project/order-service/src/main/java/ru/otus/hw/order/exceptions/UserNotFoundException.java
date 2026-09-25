package ru.otus.hw.order.exceptions;

// Thrown when billing-service reports the given userId does not exist.
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
