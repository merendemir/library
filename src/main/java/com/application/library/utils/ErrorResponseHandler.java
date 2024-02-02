package com.application.library.utils;

import java.time.LocalDateTime;

public class ErrorResponseHandler {
    private final String errorMessage;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponseHandler(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
