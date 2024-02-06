package com.application.library.listener.event;

import org.springframework.context.ApplicationEvent;

public class UpdateUserReservationCompleteStatus extends ApplicationEvent {
    private final Long bookId;
    private final Long userId;
    public UpdateUserReservationCompleteStatus(Object source, Long bookId, Long userId) {
        super(source);
        this.bookId = bookId;
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public Long getUserId() {
        return userId;
    }
}
