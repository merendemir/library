package com.application.library.data.view.shelf;

import com.application.library.data.view.book.BookView;

import java.util.Set;

public interface ShelfView {
    Long getId();
    String getName();

    Integer getCapacity();

    Integer getAvailableCapacity();

    Set<BookView> getBooks();
}
