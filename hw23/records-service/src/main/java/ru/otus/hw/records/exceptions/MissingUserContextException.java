package ru.otus.hw.records.exceptions;

public class MissingUserContextException extends RuntimeException {
    public MissingUserContextException(String message) {
        super(message);
    }
}
