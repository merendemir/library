package com.application.library.model;

import com.application.library.core.model.IntegerSoftDeleteEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


@Entity(name = "book_comment")
@Table(name = "book_comment")
@SQLDelete(sql = "UPDATE book_comment SET deleted = NULL, deleted_date_time = NOW() WHERE id = ?")
@Where(clause = "deleted = false")
public class BookComment extends IntegerSoftDeleteEntity {

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "comment_text", nullable = false)
    private String commentText;

    @Column(name = "rating", nullable = false)
    private double rating;


    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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
}
