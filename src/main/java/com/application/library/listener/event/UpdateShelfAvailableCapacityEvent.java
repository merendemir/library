package com.application.library.listener.event;

import org.springframework.context.ApplicationEvent;

public class UpdateShelfAvailableCapacityEvent extends ApplicationEvent {
    private final Long shelfId;
    public UpdateShelfAvailableCapacityEvent(Object source, Long shelfId) {
        super(source);
        this.shelfId = shelfId;
    }

    public Long getShelfId() {
        return shelfId;
    }

}
