package com.application.library.listener;

import com.application.library.listener.event.UpdateBookAvailableCountEvent;
import com.application.library.model.Book;
import com.application.library.repository.BookRepository;
import com.application.library.repository.LendTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BookListenerTest {
    private BookRepository bookRepository;
    private LendTransactionRepository lendTransactionRepository;
    private BookListener bookListener;

    @BeforeEach
    public void setUp() {
        bookRepository = mock(BookRepository.class);
        lendTransactionRepository = mock(LendTransactionRepository.class);
        bookListener = new BookListener(bookRepository, lendTransactionRepository);
    }

    @Test
    public void testUpdateBookAvailableCountEvent() {
        // given
        UpdateBookAvailableCountEvent event = new UpdateBookAvailableCountEvent(this, 1L);
        Book book = new Book();
        book.setTotalCount(5);
        when(bookRepository.findById(event.getBookId())).thenReturn(Optional.of(book));

        // when
        when(lendTransactionRepository.countAllByBook_IdAndReturnedFalse(event.getBookId())).thenReturn(2);

        // then
        bookListener.onRegistrationUserConfirmationOrderCreatedEvent(event);

        assertEquals(3, book.getAvailableCount()); // 5 total count - 2 lent count = 3 available count
        verify(bookRepository, times(1)).save(book);
    }

}