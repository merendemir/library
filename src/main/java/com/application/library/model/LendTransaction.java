package com.application.library.model;

import com.application.library.core.model.UUIDEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity(name = "lend_transaction")
@Table(name = "lend_transaction")
public class LendTransaction extends UUIDEntity {

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "lender_id", nullable = false)
    private User lender;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "dead_line_date", nullable = false)
    private LocalDate deadlineDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Column(name = "late_fee_paid")
    private Double lateFeePaid;

    private boolean returned = false;

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getLender() {
        return lender;
    }

    public void setLender(User lender) {
        this.lender = lender;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDate deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public Double getLateFeePaid() {
        return lateFeePaid;
    }

    public void setLateFeePaid(Double lateFee) {
        this.lateFeePaid = lateFee;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
