package com.application.library.data.view;

import com.application.library.core.view.IntegerEntityView;
import com.application.library.data.view.book.BookView;

import java.time.LocalDate;

public interface BookReservationView extends IntegerEntityView {

    UserListView getUser();

    BookView getBook();

    LocalDate getReservationDate();

    boolean isCompleted();
}
