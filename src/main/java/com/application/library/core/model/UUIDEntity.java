package com.application.library.core.model;

import jakarta.persistence.*;

import java.util.UUID;


@MappedSuperclass
public abstract class UUIDEntity extends TimestampEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    public UUID getId() {
        return id;
    }

}
