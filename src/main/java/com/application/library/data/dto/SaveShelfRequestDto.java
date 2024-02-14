package com.application.library.data.dto;

import java.util.Objects;

public class SaveShelfRequestDto {
    private String name;
    private Integer capacity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaveShelfRequestDto that = (SaveShelfRequestDto) o;
        return Objects.equals(name, that.name) && Objects.equals(capacity, that.capacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, capacity);
    }
}
