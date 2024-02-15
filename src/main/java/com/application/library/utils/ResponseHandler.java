package com.application.library.utils;

import java.time.LocalDateTime;

public class ResponseHandler<T> {


    private final T data;
    private final LocalDateTime timestamp;

    public ResponseHandler(T data) {
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }


    public T getData() {
        return data;
    }


    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
