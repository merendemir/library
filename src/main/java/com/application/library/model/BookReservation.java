package com.application.library.model;

import com.application.library.core.model.IntegerSoftDeleteEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;


@Entity(name = "book_reservation")
@Table(name = "book_reservation")
public class BookReservation extends IntegerSoftDeleteEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Column(name = "completed", nullable = false)
    private boolean completed = false;

    public BookReservation() {
    }

    public BookReservation(User user, Book book, LocalDate reservationDate) {
        this.user = user;
        this.book = book;
        this.reservationDate = reservationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookReservation that = (BookReservation) o;
        return completed == that.completed && Objects.equals(user, that.user) && Objects.equals(book, that.book) && Objects.equals(reservationDate, that.reservationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, book, reservationDate, completed);
    }
}
