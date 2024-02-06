package com.application.library.listener.event;

import org.springframework.context.ApplicationEvent;

import java.util.Objects;

public class UpdateShelfAvailableCapacityEvent extends ApplicationEvent {
    private final Long shelfId;
    public UpdateShelfAvailableCapacityEvent(Object source, Long shelfId) {
        super(source);
        this.shelfId = shelfId;
    }

    public Long getShelfId() {
        return shelfId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateShelfAvailableCapacityEvent that = (UpdateShelfAvailableCapacityEvent) o;
        return Objects.equals(shelfId, that.shelfId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shelfId);
    }
}
