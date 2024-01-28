package com.application.library.listener;

import com.application.library.listener.event.UpdateShelfAvailableCapacityEvent;
import com.application.library.repository.ShelfRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ShelfListener {

    private final ShelfRepository shelfRepository;

    public ShelfListener(ShelfRepository shelfRepository) {
        this.shelfRepository = shelfRepository;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void onRegistrationUserConfirmationOrderCreatedEvent(UpdateShelfAvailableCapacityEvent event) {
        Long shelfId = event.getShelfId();
        shelfRepository.findById(shelfId).ifPresent(shelf -> {
            shelf.setAvailableCapacity(shelf.getCapacity() - shelf.getBooks().size());
        });
    }
}
