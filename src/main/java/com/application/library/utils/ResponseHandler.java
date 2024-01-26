package com.application.library.utils;

import java.time.LocalDateTime;

public class ResponseHandler<T> {


    private T data;
    private LocalDateTime timestamp;

    public ResponseHandler(T data) {
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
