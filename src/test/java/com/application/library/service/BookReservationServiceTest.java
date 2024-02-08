package com.application.library.service;

import com.application.library.data.dto.BookReservationRequestDto;
import com.application.library.data.view.BookReservationView;
import com.application.library.exception.EntityAlreadyExistsException;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.helper.AuthHelper;
import com.application.library.model.Book;
import com.application.library.model.BookReservation;
import com.application.library.model.User;
import com.application.library.repository.BookReservationRepository;
import com.application.library.support.TestSupport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookReservationServiceTest extends TestSupport {

    private BookReservationRepository bookReservationRepository;
    private BookService bookService;
    private LendTransactionService lendTransactionService;
    private BookReservationService bookReservationService;

    private static MockedStatic<AuthHelper> authHelperMockedStatic;

    @BeforeAll
    static void beforeAll() {
        authHelperMockedStatic = mockStatic(AuthHelper.class);
    }

    @AfterAll
    static void afterAll() {
        authHelperMockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        bookReservationRepository = mock(BookReservationRepository.class);
        bookService = mock(BookService.class);
        lendTransactionService = mock(LendTransactionService.class);
        bookReservationService = new BookReservationService(bookReservationRepository, bookService, lendTransactionService);
    }

    @Test
    void testReserveBook_whenBookIsAvailableForSelectedDate_shouldReturnBookReservation() {
        // given
        User user = getTestUser();
        BookReservationRequestDto requestDto = new BookReservationRequestDto();
        LocalDate now = LocalDate.now();
        requestDto.setReservationDate(now.plusDays(7));
        Book book = getTestBook();
        book.setAvailableCount(5);

        Long bookId = book.getId();
        BookReservation bookReservation = new BookReservation();

        // when
        when(AuthHelper.getActiveUser()).thenReturn(user);
        when(bookReservationRepository.existsByUser_IdAndReservationDateAfter(user.getId(), now)).thenReturn(false);
        when(bookReservationRepository.existsByUser_IdAndCompletedAndCreatedAtAfter(user.getId(), false, now.minusDays(7).atTime(LocalTime.MAX))).thenReturn(false);
        when(bookService.findById(bookId)).thenReturn(book);
        when(lendTransactionService.countByBookIdAndReturnedAndDeadlineDateAfter(bookId, false, requestDto.getReservationDate())).thenReturn(0L);
        when(bookReservationRepository.countByBook_IdAndReservationDateAfterAndCompletedFalse(bookId, requestDto.getReservationDate())).thenReturn(0L);
        when(bookReservationRepository.save(any(BookReservation.class))).thenReturn(bookReservation);

        // then
        BookReservation result = bookReservationService.reserveBook(bookId, requestDto);
        assertEquals(bookReservation, result);

        verify(bookReservationRepository, times(1)).existsByUser_IdAndReservationDateAfter(user.getId(), now);
        verify(bookReservationRepository, times(1)).existsByUser_IdAndCompletedAndCreatedAtAfter(user.getId(), false, now.minusDays(7).atTime(LocalTime.MAX));
        verify(bookService, times(1)).findById(bookId);
        verify(lendTransactionService, times(1)).countByBookIdAndReturnedAndDeadlineDateAfter(bookId, false, requestDto.getReservationDate());
        verify(bookReservationRepository, times(1)).countByBook_IdAndReservationDateAfterAndCompletedFalse(bookId, requestDto.getReservationDate());
        verify(bookReservationRepository, times(1)).save(any(BookReservation.class));
    }

    @Test
    void testReserveBook_whenUserAlreadyHasReservation_shouldThrowEntityAlreadyExistsException() {
        // given
        User user = getTestUser();
        Long bookId = 1L;
        BookReservationRequestDto requestDto = new BookReservationRequestDto();

        // when
        when(AuthHelper.getActiveUser()).thenReturn(user);
        when(bookReservationRepository.existsByUser_IdAndReservationDateAfter(user.getId(), LocalDate.now())).thenReturn(true);
        when(bookReservationRepository.existsByUser_IdAndReservationDateAfter(user.getId(), LocalDate.now())).thenReturn(true);

        // then
        assertThrows(EntityAlreadyExistsException.class, () -> bookReservationService.reserveBook(bookId, requestDto));
    }

    @Test
    void testReserveBook_whenUserHasUncompletedReservation_shouldThrowEntityAlreadyExistsException() {
        // given
        User user = getTestUser();
        Long bookId = 1L;
        BookReservationRequestDto requestDto = new BookReservationRequestDto();

        // when
        when(AuthHelper.getActiveUser()).thenReturn(user);
        when(bookReservationRepository.existsByUser_IdAndReservationDateAfter(user.getId(), LocalDate.now())).thenReturn(false);
        when(bookReservationRepository.existsByUser_IdAndCompletedAndCreatedAtAfter(user.getId(), false, LocalDate.now().minusDays(7).atTime(LocalTime.MAX))).thenReturn(true);

        // then
        assertThrows(EntityAlreadyExistsException.class, () -> bookReservationService.reserveBook(bookId, requestDto));
    }

    @Test
    void testReserveBook_whenBookIsNotAvailableForSelectedDate_shouldThrowIllegalStateException() {
        // given
        User user = getTestUser();
        BookReservationRequestDto requestDto = new BookReservationRequestDto();
        LocalDate now = LocalDate.now();
        requestDto.setReservationDate(now.plusDays(7));
        Book book = getTestBook();
        book.setAvailableCount(5);

        Long bookId = book.getId();

        // when
        when(AuthHelper.getActiveUser()).thenReturn(user);
        when(bookReservationRepository.existsByUser_IdAndReservationDateAfter(user.getId(), now)).thenReturn(false);
        when(bookReservationRepository.existsByUser_IdAndCompletedAndCreatedAtAfter(user.getId(), false, now.minusDays(7).atTime(LocalTime.MAX))).thenReturn(false);
        when(bookService.findById(bookId)).thenReturn(book);
        when(lendTransactionService.countByBookIdAndReturnedAndDeadlineDateAfter(bookId, false, requestDto.getReservationDate())).thenReturn(0L);
        when(bookReservationRepository.countByBook_IdAndReservationDateAfterAndCompletedFalse(bookId, requestDto.getReservationDate())).thenReturn(5L);

        // then
        assertThrows(IllegalStateException.class, () -> bookReservationService.reserveBook(bookId, requestDto));

        verify(bookReservationRepository, times(1)).existsByUser_IdAndReservationDateAfter(user.getId(), now);
        verify(bookReservationRepository, times(1)).existsByUser_IdAndCompletedAndCreatedAtAfter(user.getId(), false, now.minusDays(7).atTime(LocalTime.MAX));
        verify(bookService, times(1)).findById(bookId);
        verify(lendTransactionService, times(1)).countByBookIdAndReturnedAndDeadlineDateAfter(bookId, false, requestDto.getReservationDate());
        verify(bookReservationRepository, times(1)).countByBook_IdAndReservationDateAfterAndCompletedFalse(bookId, requestDto.getReservationDate());
        verify(bookReservationRepository, times(0)).save(any(BookReservation.class));
    }

    @Test
    void testGetAuthenticationUserReservations_whenTestGetAuthenticationUserReservationsCalledWithPageAndSize_shouldReturnPageOfBookReservationView() {
        // given
        int page = 0;
        int size = 10;
        User user = getTestUser();
        BookReservationView bookReservation = getBookReservationView();
        PageRequest pageRequest = PageRequest.of(page, size);
        PageImpl<BookReservationView> bookReservationPage = new PageImpl<>(List.of(bookReservation));

        // when
        when(AuthHelper.getActiveUser()).thenReturn(user);
        when(bookReservationRepository.findByUser_Id(user.getId(), pageRequest)).thenReturn(bookReservationPage);

        // then
        Page<BookReservationView> result = bookReservationService.getAuthenticationUserReservations(page, size);
        assertEquals(bookReservationPage, result);

        verify(bookReservationRepository, times(1)).findByUser_Id(user.getId(), pageRequest);
    }

    @Test
    void testUpdateReservation_whenReservationIsNotCompletedAndBookIsAvailableForSelectedDate_shouldReturnBookReservation() {
        // given
        Long id = 1L;
        BookReservationRequestDto requestDto = new BookReservationRequestDto();
        requestDto.setReservationDate(LocalDate.now().plusDays(7));
        BookReservation bookReservation = new BookReservation();
        bookReservation.setCompleted(false);
        Book testBook = getTestBook();
        Long bookId = testBook.getId();
        bookReservation.setBook(testBook);

        // when
        when(bookReservationRepository.findById(id)).thenReturn(Optional.of(bookReservation));
        when(lendTransactionService.countByBookIdAndReturnedAndDeadlineDateAfter(bookId, false, requestDto.getReservationDate())).thenReturn(5L);
        when(bookReservationRepository.countByBook_IdAndReservationDateAfterAndCompletedFalse(bookId, requestDto.getReservationDate())).thenReturn(0L);

        // then
        BookReservation result = bookReservationService.updateReservation(id, requestDto);
        assertEquals(bookReservation, result);

        verify(bookReservationRepository, times(1)).findById(id);
        verify(lendTransactionService, times(1)).countByBookIdAndReturnedAndDeadlineDateAfter(bookId, false, requestDto.getReservationDate());
        verify(bookReservationRepository, times(1)).countByBook_IdAndReservationDateAfterAndCompletedFalse(bookId, requestDto.getReservationDate());
    }

    @Test
    void testUpdateReservation_whenReservationIdNotExists_shouldThrowEntityNotFoundException() {
        // given
        Long id = 1L;
        BookReservationRequestDto requestDto = new BookReservationRequestDto();

        // when
        when(bookReservationRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> bookReservationService.updateReservation(id, requestDto));

        verify(bookReservationRepository, times(1)).findById(id);
    }

    @Test
    void testUpdateReservation_whenReservationIsNotCompletedAndBookIsNotAvailableForSelectedDate_shouldThrowIllegalStateException() {
        // given
        Long id = 1L;
        BookReservationRequestDto requestDto = new BookReservationRequestDto();
        requestDto.setReservationDate(LocalDate.now().plusDays(7));
        BookReservation bookReservation = new BookReservation();
        bookReservation.setCompleted(false);
        Book testBook = getTestBook();
        Long bookId = testBook.getId();
        bookReservation.setBook(testBook);

        // when
        when(bookReservationRepository.findById(id)).thenReturn(Optional.of(bookReservation));
        when(lendTransactionService.countByBookIdAndReturnedAndDeadlineDateAfter(bookId, false, requestDto.getReservationDate())).thenReturn(5L);
        when(bookReservationRepository.countByBook_IdAndReservationDateAfterAndCompletedFalse(bookId, requestDto.getReservationDate())).thenReturn(5L);

        // then
        assertThrows(IllegalStateException.class, () -> bookReservationService.updateReservation(id, requestDto));

        verify(bookReservationRepository, times(1)).findById(id);
        verify(lendTransactionService, times(1)).countByBookIdAndReturnedAndDeadlineDateAfter(bookId, false, requestDto.getReservationDate());
        verify(bookReservationRepository, times(1)).countByBook_IdAndReservationDateAfterAndCompletedFalse(bookId, requestDto.getReservationDate());
    }


    @Test
    void testUpdateReservation_whenReservationIsCompleted_shouldThrowIllegalStateException() {
        // given
        Long id = 1L;
        BookReservationRequestDto requestDto = new BookReservationRequestDto();
        BookReservation bookReservation = new BookReservation();
        bookReservation.setCompleted(true);

        // when
        when(bookReservationRepository.findById(id)).thenReturn(Optional.of(bookReservation));

        // then
        assertThrows(IllegalStateException.class, () -> bookReservationService.updateReservation(id, requestDto));

        verify(bookReservationRepository, times(1)).findById(id);
    }

    @Test
    void testCancelReservation_whenReservationIsNotCompleted_shouldReturnBookReservation() {
        // given
        Long id = 1L;
        BookReservation bookReservation = new BookReservation();
        bookReservation.setCompleted(false);

        // when
        when(bookReservationRepository.findById(id)).thenReturn(Optional.of(bookReservation));

        // then
        BookReservation result = bookReservationService.cancelReservation(id);
        assertEquals(bookReservation, result);

        verify(bookReservationRepository, times(1)).findById(id);
        verify(bookReservationRepository, times(1)).delete(bookReservation);
    }

    @Test
    void testCancelReservation_whenReservationIsCompleted_shouldThrowIllegalStateException() {
        // given
        Long id = 1L;
        BookReservation bookReservation = new BookReservation();
        bookReservation.setCompleted(true);

        // when
        when(bookReservationRepository.findById(id)).thenReturn(Optional.of(bookReservation));

        // then
        assertThrows(IllegalStateException.class, () -> bookReservationService.cancelReservation(id));

        verify(bookReservationRepository, times(1)).findById(id);
        verify(bookReservationRepository, times(0)).delete(bookReservation);
    }

    @Test
    void testCancelReservation_whenReservationIdNotExists_shouldThrowEntityNotFoundException() {
        // given
        Long id = 1L;

        // when
        when(bookReservationRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> bookReservationService.cancelReservation(id));

        verify(bookReservationRepository, times(1)).findById(id);
        verify(bookReservationRepository, times(0)).delete(any(BookReservation.class));
    }

}