package com.application.library.listener;

import com.application.library.listener.event.UpdateBookAvailableCountEvent;
import com.application.library.repository.BookRepository;
import com.application.library.repository.LendTransactionRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BookListener {

    private final BookRepository bookRepository;
    private final LendTransactionRepository lendTransactionRepository;

    public BookListener(BookRepository bookRepository, LendTransactionRepository lendTransactionRepository) {
        this.bookRepository = bookRepository;
        this.lendTransactionRepository = lendTransactionRepository;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void onRegistrationUserConfirmationOrderCreatedEvent(UpdateBookAvailableCountEvent event) {
        Long bookId = event.getBookId();
        bookRepository.findById(bookId).ifPresent(book -> {
            int lentCount = lendTransactionRepository.countAllByBook_IdAndReturnedFalse(bookId);

            int availableCount = book.getTotalCount() - lentCount;

            book.setAvailableCount(availableCount);

            bookRepository.save(book);
        });
    }
}
