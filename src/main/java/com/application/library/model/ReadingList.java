package com.application.library.model;

import com.application.library.core.model.IntegerEntity;
import jakarta.persistence.*;

import java.util.Set;


@Entity(name = "reading_list")
@Table(name = "reading_list")
public class ReadingList extends IntegerEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "reading_list_books",
            joinColumns = @JoinColumn(name = "reading_list_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<Book> books;

    public ReadingList() {
    }

    public ReadingList(User user, Set<Book> books) {
        this.user = user;
        this.books = books;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}
