package com.application.library.listener;

import com.application.library.listener.event.UpdateUserReservationCompleteStatus;
import com.application.library.model.BookReservation;
import com.application.library.repository.BookReservationRepository;
import com.application.library.support.TestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookReservationListenerTest extends TestSupport {

    private BookReservationRepository bookReservationRepository;
    private BookReservationListener bookReservationListener;

    @BeforeEach
    public void setUp() {
        bookReservationRepository = mock(BookReservationRepository.class);
        bookReservationListener = new BookReservationListener(bookReservationRepository);
    }

    @Test
    public void testUpdateUserReservationCompleteStatusEvent() {
        // given
        BookReservation bookReservation = getTestBookReservation();
        UpdateUserReservationCompleteStatus event = new UpdateUserReservationCompleteStatus(this,bookReservation.getBook().getId(), bookReservation.getUser().getId());
        when(bookReservationRepository.findByBook_IdAndUser_IdAndCompletedFalse(event.getBookId(), event.getUserId()))
                .thenReturn(Optional.of(bookReservation));

        // when
        bookReservationListener.onRegistrationUserConfirmationOrderCreatedEvent(event);

        // then
        assertTrue(bookReservation.isCompleted());
        verify(bookReservationRepository, times(1)).save(bookReservation);
    }
}