package com.application.library.exception;

public class ShelfFullException extends RuntimeException{

    public ShelfFullException(String message) {
        super(message);
    }
}
