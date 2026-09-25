package ru.otus.hw.order.exceptions;

// Wraps an unexpected (non-4xx) failure from billing-service or notification-service.
public class UpstreamServiceException extends RuntimeException {
    public UpstreamServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
