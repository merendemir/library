package com.application.library.data.view;

import java.util.Set;

public interface ShelfView {
    Long getId();
    String getName();

    Integer getCapacity();

    Integer getAvailableCapacity();

    Set<BookView> getBooks();
}
