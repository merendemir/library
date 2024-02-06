package com.application.library.data.view;

import com.application.library.core.view.IntegerEntityView;
import com.application.library.data.view.book.BookView;

import java.time.LocalDate;

public interface BookReservationView extends IntegerEntityView {

    UserView getUser();

    BookView getBooks();

    LocalDate getReservationDate();

    boolean isCompleted();
}
