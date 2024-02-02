package com.application.library.core.model;

import jakarta.persistence.*;


@MappedSuperclass
public abstract class IntegerEntity extends TimestampEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    public Long getId() {
        return id;
    }

}
