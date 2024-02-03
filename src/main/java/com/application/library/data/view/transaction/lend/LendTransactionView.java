package com.application.library.data.view.transaction.lend;

import com.application.library.core.view.UUIDEntityView;
import com.application.library.data.view.UserView;
import com.application.library.data.view.book.BaseBookView;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface LendTransactionView extends UUIDEntityView {
    LocalDate getDeadlineDate();

    LocalDateTime getReturnDate();

    Double getLateFeePaid();

    boolean isReturned();

    BaseBookView getBook();

    UserView getUser();

    UserView getLender();
}
