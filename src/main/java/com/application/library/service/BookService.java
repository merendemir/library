package com.application.library.service;

import com.application.library.constants.MessageConstants;
import com.application.library.converter.BookConverter;
import com.application.library.data.dto.CreateBookRequestDto;
import com.application.library.data.dto.SaveBookRequestDto;
import com.application.library.data.view.book.BookView;
import com.application.library.exception.EntityAlreadyExistsException;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.listener.event.UpdateShelfAvailableCapacityEvent;
import com.application.library.model.Book;
import com.application.library.model.Shelf;
import com.application.library.repository.BookRepository;
import com.application.library.repository.LendTransactionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookConverter bookConverter;
    private final ShelfService shelfService;
    private final LendTransactionRepository lendTransactionRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public BookService(BookRepository bookRepository, BookConverter bookConverter, ShelfService shelfService, LendTransactionRepository lendTransactionRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.bookRepository = bookRepository;
        this.bookConverter = bookConverter;
        this.shelfService = shelfService;
        this.lendTransactionRepository = lendTransactionRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public Book saveBook(CreateBookRequestDto requestDto) {
        if (existsByIsbn(requestDto.getIsbn()))
            throw new EntityAlreadyExistsException(MessageConstants.BOOK_ALREADY_EXISTS_WITH_ISBN);

        Book book = bookConverter.toEntity(requestDto);
        shelfService.checkShelfCapacity(book.getShelf());
        applicationEventPublisher.publishEvent(new UpdateShelfAvailableCapacityEvent(this, book.getShelf().getId()));

        book.setAvailableCount(book.getTotalCount());

        return bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(MessageConstants.BOOK_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public BookView getBookById(Long id) {
        return bookRepository.getBookById(id).orElseThrow(() -> new EntityNotFoundException(MessageConstants.BOOK_NOT_FOUND));
    }

    @Transactional
    public Long deleteBook(Long id) {
        bookRepository.delete(findById(id));
        return id;
    }

    @Transactional
    public Book updateBook(Long bookId, SaveBookRequestDto requestDto) {
        Book book = findById(bookId);
        int lendBookCount = lendTransactionRepository.countAllByBook_IdAndReturnedFalse(book.getId());

        if (requestDto.getTotalCount() < lendBookCount)
            throw new IllegalArgumentException(MessageConstants.BOOK_WILL_BE_LESS_THAN_LEND_BOOK_COUNT);

        return bookConverter.updateEntity(requestDto, book);
    }

    @Transactional(readOnly = true)
    public Page<BookView> getAllBooks(int page, int size, Optional<String> sortParam, Optional<Sort.Direction> direction) {
        PageRequest pageRequest = getPageRequest(page, size, sortParam, direction);
        return bookRepository.getAllBy(pageRequest);
    }

    @Transactional
    public Book moveBook(Long bookId, Long shelfId) {
        Book book = findById(bookId);
        Shelf newShelf = shelfService.findById(shelfId);
        shelfService.checkShelfCapacity(newShelf);

        applicationEventPublisher.publishEvent(new UpdateShelfAvailableCapacityEvent(this, book.getShelf().getId()));
        applicationEventPublisher.publishEvent(new UpdateShelfAvailableCapacityEvent(this, shelfId));

        book.setShelf(newShelf);
        return book;
    }

    @Transactional(readOnly = true)
    public Page<BookView> findBooksByShelfId(Long shelfId, int page, int size) {
        return bookRepository.findAllByShelfId(shelfId, PageRequest.of(page, size));
    }

    private PageRequest getPageRequest(int page, int size, Optional<String> sortParam, Optional<Sort.Direction> direction) {
        return sortParam.isPresent() && direction.isPresent() ?
                PageRequest.of(page, size, direction.get(), sortParam.get()) :
                PageRequest.of(page, size, Sort.Direction.ASC, "name");
    }

    private boolean existsByIsbn(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }


}
