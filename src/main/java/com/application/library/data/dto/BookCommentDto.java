package com.application.library.data.dto;

import java.time.LocalDateTime;

public class BookCommentDto {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String commentText;
    private Double rating;
    private BookUserCommentUser user;

    public BookCommentDto() {
    }

    public BookCommentDto(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String commentText, Double rating, String firstName, String lastName) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.commentText = commentText;
        this.rating = rating;
        this.user = new BookUserCommentUser(firstName, lastName);
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCommentText() {
        return commentText;
    }

    public Double getRating() {
        return rating;
    }

    public BookUserCommentUser getUser() {
        return user;
    }

    public class BookUserCommentUser {
        private final String firstName;
        private final String lastName;

        public BookUserCommentUser(String firstName, String lastName) {
            this.firstName = firstName.substring(0, 2) + "***";
            this.lastName = lastName.substring(0, 2) + "***";
        }


        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }
}
