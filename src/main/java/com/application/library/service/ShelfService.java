package com.application.library.service;


import com.application.library.constants.MessageConstants;
import com.application.library.converter.ShelfConverter;
import com.application.library.data.dto.SaveShelfRequestDto;
import com.application.library.data.view.shelf.ShelfBaseView;
import com.application.library.data.view.shelf.ShelfView;
import com.application.library.exception.EntityAlreadyExistsException;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.exception.IllegalDeleteOperationException;
import com.application.library.exception.ShelfFullException;
import com.application.library.listener.event.UpdateShelfAvailableCapacityEvent;
import com.application.library.model.Shelf;
import com.application.library.repository.ShelfRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShelfService {

    private final ShelfRepository ShelfRepository;
    private final ShelfConverter shelfConverter;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ShelfService(ShelfRepository ShelfRepository, ShelfConverter shelfConverter, ApplicationEventPublisher applicationEventPublisher) {
        this.ShelfRepository = ShelfRepository;
        this.shelfConverter = shelfConverter;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public Shelf saveShelf(SaveShelfRequestDto requestDto) {
        if (existsByName(requestDto.getName())) throw new EntityAlreadyExistsException(MessageConstants.SHELF_ALREADY_EXISTS_WITH_NAME);

        Shelf shelf = shelfConverter.toEntity(requestDto);
        shelf.setAvailableCapacity(shelf.getCapacity());
        return ShelfRepository.save(shelf);
    }

    @Transactional(readOnly = true)
    public Shelf findById(Long id) {
        return ShelfRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(MessageConstants.SHELF_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public ShelfView getShelfById(Long id) {
        return ShelfRepository.getShelfById(id).orElseThrow(() -> new EntityNotFoundException(MessageConstants.SHELF_NOT_FOUND));
    }

    @Transactional
    public Long deleteShelf(Long id) {
        Shelf shelf = findById(id);
        if (!shelf.getBooks().isEmpty()) throw new IllegalDeleteOperationException(MessageConstants.SHELF_CANNOT_BE_DELETED);

        ShelfRepository.delete(shelf);
        return id;
    }

    @Transactional
    public Shelf updateShelf(Long ShelfId, SaveShelfRequestDto requestDto) {
        Shelf shelf = shelfConverter.updateEntity(requestDto, findById(ShelfId));
        checkShelfCapacity(shelf, requestDto.getCapacity());

        applicationEventPublisher.publishEvent(new UpdateShelfAvailableCapacityEvent(this, shelf.getId()));

        return shelf;
    }

    @Transactional(readOnly = true)
    public Page<ShelfBaseView> getAllShelf(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "name");
        return ShelfRepository.getAllBy(pageRequest);
    }

    public void checkShelfCapacity(Shelf shelf) {
        if (shelf.getAvailableCapacity() <= 0) {
            throw new ShelfFullException(MessageConstants.SHELF_FULL);
        }
    }

    public void checkShelfCapacity(Shelf shelf, int newCapacity) {
        if (shelf.getBooks().size() > newCapacity) {
            throw new ShelfFullException(MessageConstants.SHELF_WILL_FULL);
        }
    }

    private boolean existsByName(String name) {
        return ShelfRepository.existsByName(name);
    }
}
