package com.application.library.data.view.book;

import com.application.library.core.view.IntegerEntityView;
import com.application.library.data.view.shelf.ShelfBaseView;

public interface BookView extends IntegerEntityView {

    String getName();

    String getAuthor();

    String getIsbn();

    Integer getPageCount();

    String getPublisher();

    String getPublishedAt();

    String getLanguage();

    String getDescription();

    String getImageUrl();

    Integer getTotalCount();

    Integer getAvailableCount();

    default Boolean getIsAvailable() {
        return getAvailableCount() > 0;
    }

    ShelfBaseView getShelf();
}
