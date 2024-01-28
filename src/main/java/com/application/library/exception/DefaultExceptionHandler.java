package com.application.library.exception;

import com.application.library.utils.ResponseHandler;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseHandler<String>> handleGlobalExceptions(Exception exception) {
        exception.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseHandler<>(exception.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Void> handleAuthenticationExceptions() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseHandler<String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Throwable cause = ex;

        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        String message = cause.getMessage();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseHandler<>("Data Integrity Violation: " + message));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseHandler<String>> handleAccessDeniedExceptions(AccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseHandler<>(exception.getMessage()));
    }

    @ExceptionHandler(IllegalDeleteOperationException.class)
    public ResponseEntity<ResponseHandler<String>> handleIllegalDeleteOperationExceptions(IllegalDeleteOperationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseHandler<>(exception.getMessage()));
    }

}