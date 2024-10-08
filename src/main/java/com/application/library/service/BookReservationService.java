package com.application.library.service;

import com.application.library.constants.MessageConstants;
import com.application.library.data.dto.BookReservationRequestDto;
import com.application.library.data.view.BookReservationView;
import com.application.library.exception.EntityAlreadyExistsException;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.helper.AuthHelper;
import com.application.library.model.Book;
import com.application.library.model.BookReservation;
import com.application.library.model.User;
import com.application.library.repository.BookReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class BookReservationService {

    private final BookReservationRepository bookReservationRepository;
    private final BookService bookService;
    private final LendTransactionService lendTransactionService;

    public BookReservationService(BookReservationRepository bookReservationRepository, BookService bookService, LendTransactionService lendTransactionService) {
        this.bookReservationRepository = bookReservationRepository;
        this.bookService = bookService;
        this.lendTransactionService = lendTransactionService;
    }

    @Transactional
    public BookReservation reserveBook(Long bookId, BookReservationRequestDto requestDto) {
        User user = AuthHelper.getActiveUser();

        if (bookReservationRepository.existsByUser_IdAndReservationDateAfter(user.getId(), LocalDate.now()))
            throw new EntityAlreadyExistsException(MessageConstants.USER_ALREADY_HAS_A_RESERVATION);

        if (bookReservationRepository.existsByUser_IdAndCompletedAndCreatedAtAfter(user.getId(), false, LocalDate.now().minusDays(7).atTime(LocalTime.MAX)))
            throw new EntityAlreadyExistsException(MessageConstants.USER_HAS_AN_UNCOMPLETED_RESERVATION);


        Book book = bookService.findById(bookId);
        if (!isBookAvailableForDate(book, requestDto.getReservationDate()))
            throw new IllegalStateException(MessageConstants.BOOK_IS_NOT_AVAILABLE_FOR_THE_SELECTED_DATE);

        return bookReservationRepository.save(new BookReservation(user, book, requestDto.getReservationDate()));
    }

    @Transactional(readOnly = true)
    public Page<BookReservationView> getAuthenticationUserReservations(int page, int size) {
        User user = AuthHelper.getActiveUser();
        return bookReservationRepository.findByUser_Id(user.getId(), PageRequest.of(page, size));
    }

    @Transactional
    public BookReservation updateReservation(Long id, BookReservationRequestDto requestDto) {
        BookReservation bookReservation = bookReservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

        if (bookReservation.isCompleted()) throw new IllegalStateException(MessageConstants.RESERVATION_ALREADY_COMPLETED);


        if (!isBookAvailableForDate(bookReservation.getBook(), requestDto.getReservationDate()))
            throw new IllegalStateException(MessageConstants.BOOK_IS_NOT_AVAILABLE_FOR_THE_SELECTED_DATE);

        bookReservation.setReservationDate(requestDto.getReservationDate());

        return bookReservation;
    }

    @Transactional
    public BookReservation cancelReservation(Long id) {
        BookReservation bookReservation = bookReservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

        if (bookReservation.isCompleted()) throw new IllegalStateException(MessageConstants.RESERVATION_ALREADY_COMPLETED);

        bookReservationRepository.delete(bookReservation);

        return bookReservation;
    }

    private boolean isBookAvailableForDate(Book book, LocalDate date) {
        long totalAvailableCount = book.getAvailableCount()
                + lendTransactionService.countByBookIdAndReturnedAndDeadlineDateAfter(book.getId(), false, date)
                - bookReservationRepository.countByBook_IdAndReservationDateAfterAndCompletedFalse(book.getId(), date);
        return totalAvailableCount > 0;
    }

}
