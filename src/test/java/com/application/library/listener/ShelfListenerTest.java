package com.application.library.listener;

import com.application.library.listener.event.UpdateShelfAvailableCapacityEvent;
import com.application.library.model.Book;
import com.application.library.model.Shelf;
import com.application.library.repository.ShelfRepository;
import com.application.library.support.TestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShelfListenerTest extends TestSupport {
    private ShelfRepository shelfRepository;
    private ShelfListener shelfListener;

    @BeforeEach
    public void setUp() {
        shelfRepository = mock(ShelfRepository.class);
        shelfListener = new ShelfListener(shelfRepository);
    }

    @Test
    public void testUpdateShelfAvailableCapacityEvent() {
        // given
        Shelf shelf = getTestShelf();
        shelf.setCapacity(10);

        Set<Book> books = new HashSet<>();
        books.add(new Book());
        books.add(new Book());
        shelf.setBooks(books);

        UpdateShelfAvailableCapacityEvent event = new UpdateShelfAvailableCapacityEvent(this, shelf.getId());
        when(shelfRepository.findById(event.getShelfId())).thenReturn(Optional.of(shelf));

        // when
        shelfListener.onRegistrationUserConfirmationOrderCreatedEvent(event);

        // then
        assertEquals(8, shelf.getAvailableCapacity()); // 10 capacity - 2 books = 8 available capacity
    }
}