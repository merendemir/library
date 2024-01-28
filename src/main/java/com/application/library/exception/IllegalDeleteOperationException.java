package com.application.library.exception;

public class IllegalDeleteOperationException extends RuntimeException{

    public IllegalDeleteOperationException(String message) {
        super(message);
    }
}
