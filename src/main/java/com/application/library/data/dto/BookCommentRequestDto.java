package com.application.library.data.dto;

import java.util.Objects;

public class BookCommentRequestDto {

    private String commentText;
    private double rating;

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookCommentRequestDto that = (BookCommentRequestDto) o;
        return Double.compare(rating, that.rating) == 0 && Objects.equals(commentText, that.commentText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentText, rating);
    }
}
