package com.application.library.data.dto;

public class CreateBookRequestDto extends SaveBookRequestDto {
    private Long shelfId;

    public Long getShelfId() {
        return shelfId;
    }

    public void setShelfId(Long shelfId) {
        this.shelfId = shelfId;
    }
}
