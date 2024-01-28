package com.application.library.data.view;

import com.application.library.core.view.BaseIntegerEntityView;

public interface BookView extends BaseIntegerEntityView {

    String getName();

    String getAuthor();

    String getIsbn();

    Integer getPageCount();

    String getPublisher();

    String getPublishedAt();

    String getLanguage();

    String getDescription();

    String getImageUrl();

    Integer getAvailableCount();

    Boolean getIsAvailable();

    ShelfBaseView getShelf();
}
