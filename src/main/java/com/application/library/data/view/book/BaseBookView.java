package com.application.library.data.view.book;

import com.application.library.core.view.IntegerEntityView;

public interface BaseBookView extends IntegerEntityView {

    String getName();

    String getAuthor();

    String getIsbn();

    String getPageCount();

    String getPublisher();

    String getLanguage();

    String getDescription();

    String getImageUrl();
}
