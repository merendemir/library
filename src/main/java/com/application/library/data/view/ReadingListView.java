package com.application.library.data.view;

import com.application.library.core.view.IntegerEntityView;
import com.application.library.data.view.book.BookView;

import java.util.Set;

public interface ReadingListView extends IntegerEntityView {

        UserListView getUser();

        Set<BookView> getBooks();

}
