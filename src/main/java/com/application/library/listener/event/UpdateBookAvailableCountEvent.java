package com.application.library.listener.event;

import org.springframework.context.ApplicationEvent;

public class UpdateBookAvailableCountEvent extends ApplicationEvent {
    private final Long bookId;
    public UpdateBookAvailableCountEvent(Object source, Long bookId) {
        super(source);
        this.bookId = bookId;
    }

    public Long getBookId() {
        return bookId;
    }
}
