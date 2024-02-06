package com.application.library.listener;

import com.application.library.listener.event.UpdateUserReservationCompleteStatus;
import com.application.library.repository.BookReservationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BookReservationListener {

    private final BookReservationRepository bookReservationRepository;

    public BookReservationListener(BookReservationRepository bookReservationRepository) {
        this.bookReservationRepository = bookReservationRepository;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void onRegistrationUserConfirmationOrderCreatedEvent(UpdateUserReservationCompleteStatus event) {
        bookReservationRepository.findByBook_IdAndUser_IdAndCompletedFalse(event.getBookId(), event.getUserId())
                .ifPresent(bookReservation -> {
                    bookReservation.setCompleted(true);
                    bookReservationRepository.save(bookReservation);
                });

    }
}
