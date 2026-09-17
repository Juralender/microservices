package ru.otus.hw.auth.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.otus.hw.auth.exceptions.AccessDeniedException;
import ru.otus.hw.auth.exceptions.InvalidCredentialsException;
import ru.otus.hw.auth.exceptions.InvalidTokenException;
import ru.otus.hw.auth.exceptions.UserAlreadyExistsException;
import ru.otus.hw.auth.exceptions.UserNotFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(basePackages = "ru.otus.hw.auth.rest.controllers")
public class RestExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ProblemDetail handleInvalidToken(InvalidTokenException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }
}
