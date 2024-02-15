package com.application.library.data.dto;

import java.util.Objects;

public class CreateBookRequestDto extends SaveBookRequestDto {
    private Long shelfId;

    public Long getShelfId() {
        return shelfId;
    }

    public void setShelfId(Long shelfId) {
        this.shelfId = shelfId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CreateBookRequestDto that = (CreateBookRequestDto) o;
        return Objects.equals(shelfId, that.shelfId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), shelfId);
    }
}
