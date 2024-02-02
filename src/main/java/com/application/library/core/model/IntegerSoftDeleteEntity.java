package com.application.library.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;


@MappedSuperclass
public abstract class IntegerSoftDeleteEntity extends IntegerEntity {

    @Column(name = "deleted_date_time")
    private LocalDateTime deletedDateTime;

    @Column(name = "deleted")
    private Boolean deleted = false;

    public LocalDateTime getDeletedDateTime() {
        return deletedDateTime;
    }

    public Boolean isDeleted() {
        return deleted;
    }
}
