package com.application.library.service;

import com.application.library.converter.BookConverter;
import com.application.library.data.dto.CreateBookRequestDto;
import com.application.library.data.dto.SaveBookRequestDto;
import com.application.library.data.view.book.BookView;
import com.application.library.exception.EntityAlreadyExistsException;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.model.Book;
import com.application.library.model.Shelf;
import com.application.library.repository.BookRepository;
import com.application.library.repository.LendTransactionRepository;
import com.application.library.support.TestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BookServiceTest extends TestSupport {

    private BookRepository bookRepository;
    private BookConverter bookConverter;
    private ShelfService shelfService;
    private LendTransactionRepository lendTransactionRepository;
    private ApplicationEventPublisher applicationEventPublisher;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        bookConverter = mock(BookConverter.class);
        shelfService = mock(ShelfService.class);
        lendTransactionRepository = mock(LendTransactionRepository.class);
        applicationEventPublisher = mock(ApplicationEventPublisher.class);
        bookService = new BookService(bookRepository, bookConverter, shelfService, lendTransactionRepository, applicationEventPublisher);
    }

    @Test
    void testSaveBook_whenSaveBookCalledWithCreateBookRequestDto_shouldReturnBook() {
        // given
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto();
        createBookRequestDto.setIsbn("isbn");
        Book testBook = getTestBook();


        // when
        when(bookRepository.existsByIsbn(createBookRequestDto.getIsbn())).thenReturn(false);
        when(bookConverter.toEntity(createBookRequestDto)).thenReturn(testBook);
        when(bookRepository.save(testBook)).thenReturn(testBook);
        // then

        Book result = bookService.saveBook(createBookRequestDto);
        assertEquals(testBook, result);

        verify(bookRepository, times(1)).existsByIsbn(createBookRequestDto.getIsbn());
        verify(bookConverter, times(1)).toEntity(createBookRequestDto);
        verify(bookRepository, times(1)).save(testBook);
    }

    @Test
    void testSaveBook_whenSaveBookCalledWithExistsIsbn_shouldThrowEntityAlreadyExistsException() {
        // given
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto();
        createBookRequestDto.setIsbn("isbn");

        // when
        when(bookRepository.existsByIsbn(createBookRequestDto.getIsbn())).thenReturn(true);

        // then
        assertThatThrownBy(() -> bookService.saveBook(createBookRequestDto))
                .isInstanceOf(EntityAlreadyExistsException.class);

        verify(bookRepository, times(1)).existsByIsbn(createBookRequestDto.getIsbn());
    }

    @Test
    void testFindById_whenFindByIdCalledWithId_shouldReturnBook() {
        // given
        Long id = 1L;
        Book testBook = getTestBook();

        // when
        when(bookRepository.findById(id)).thenReturn(Optional.of(testBook));

        // then
        Book result = bookService.findById(id);
        assertEquals(testBook, result);

        verify(bookRepository, times(1)).findById(id);
    }

    @Test
    void testFindById_whenFindByIdCalledWithNonExistingId_shouldThrowEntityNotFoundException() {
        // given
        Long id = 1L;

        // when
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> bookService.findById(id))
                .isInstanceOf(EntityNotFoundException.class);

        verify(bookRepository, times(1)).findById(id);
    }

    @Test
    void testGetBookById_whenGetBookCalledWithId_shouldReturnBookView() {
        // given
        Long id = 1L;
        BookView testBook = getTestBookView();

        // when
        when(bookRepository.getBookById(id)).thenReturn(Optional.of(testBook));

        // then
        BookView result = bookService.getBookById(id);
        assertEquals(testBook, result);

        verify(bookRepository, times(1)).getBookById(id);
    }

    @Test
    void testGetBookById_whenGetBookNonExistingId_shouldThrowEntityNotFoundException() {
        // given
        Long id = 1L;

        // when
        when(bookRepository.getBookById(id)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> bookService.getBookById(id))
                .isInstanceOf(EntityNotFoundException.class);

        verify(bookRepository, times(1)).getBookById(id);
    }

    @Test
    void testDeleteBook_whenDeleteBookCalledWithId_shouldReturnId() {
        // given
        Long id = 1L;
        Book testBook = getTestBook();

        // when
        when(bookRepository.findById(id)).thenReturn(Optional.of(testBook));

        // then
        Long result = bookService.deleteBook(id);
        assertEquals(id, result);

        verify(bookRepository, times(1)).findById(id);
        verify(bookRepository, times(1)).delete(testBook);
    }

    @Test
    void testDeleteBook_whenDeleteBookCalledWithNonExistingId_shouldThrowEntityNotFoundException() {
        // given
        Long id = 1L;

        // when
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> bookService.deleteBook(id))
                .isInstanceOf(EntityNotFoundException.class);

        verify(bookRepository, times(1)).findById(id);
    }

    @Test
    void testUpdateBook_whenUpdateBookCalledWithBookIdAndSaveBookRequestDto_shouldReturnBook() {
        // given
        Long bookId = 1L;
        Book testBook = getTestBook();
        SaveBookRequestDto saveBookRequestDto = new SaveBookRequestDto();
        saveBookRequestDto.setTotalCount(10);

        // when
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(lendTransactionRepository.countAllByBook_IdAndReturnedFalse(testBook.getId())).thenReturn(0);
        when(bookConverter.updateEntity(saveBookRequestDto, testBook)).thenReturn(testBook);

        // then
        Book result = bookService.updateBook(bookId, saveBookRequestDto);
        assertEquals(testBook, result);

        verify(bookRepository, times(1)).findById(bookId);
        verify(lendTransactionRepository, times(1)).countAllByBook_IdAndReturnedFalse(testBook.getId());
    }

    @Test
    void testUpdateBook_whenUpdateBookCalledWithBookIdAndSaveBookRequestDtoAndTotalCountLessThanLendBookCount_shouldThrowIllegalArgumentException() {
        // given
        Long bookId = 1L;
        Book testBook = getTestBook();
        SaveBookRequestDto saveBookRequestDto = new SaveBookRequestDto();
        saveBookRequestDto.setTotalCount(5);

        // when
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(lendTransactionRepository.countAllByBook_IdAndReturnedFalse(testBook.getId())).thenReturn(10);

        // then
        assertThatThrownBy(() -> bookService.updateBook(bookId, saveBookRequestDto))
                .isInstanceOf(IllegalArgumentException.class);

        verify(bookRepository, times(1)).findById(bookId);
        verify(lendTransactionRepository, times(1)).countAllByBook_IdAndReturnedFalse(testBook.getId());
    }

    @Test
    void testUpdateBook_whenUpdateBookCalledWithNonExistingBookId_shouldThrowEntityNotFoundException() {
        // given
        Long bookId = 1L;
        SaveBookRequestDto saveBookRequestDto = new SaveBookRequestDto();

        // when
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> bookService.updateBook(bookId, saveBookRequestDto))
                .isInstanceOf(EntityNotFoundException.class);

        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void testMoveBook_whenMoveBookCalledWithBookIdAndShelfId_shouldReturnBook() {
        // given
        Long bookId = 1L;
        Long shelfId = 1L;
        Book testBook = getTestBook();
        Shelf testShelf = getTestShelf();

        // when
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(shelfService.findById(shelfId)).thenReturn(testShelf);

        // then
        Book result = bookService.moveBook(bookId, shelfId);
        assertEquals(testBook, result);

        verify(bookRepository, times(1)).findById(bookId);
        verify(shelfService, times(1)).checkShelfCapacity(testBook.getShelf());
        verify(applicationEventPublisher, times(2)).publishEvent(any());
    }

    @Test
    void testMoveBook_whenMoveBookCalledWithNonExistingBookId_shouldThrowEntityNotFoundException() {
        // given
        Long bookId = 1L;
        Long shelfId = 1L;

        // when
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> bookService.moveBook(bookId, shelfId))
                .isInstanceOf(EntityNotFoundException.class);

        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void testGetAllBooks_whenGetAllBooksCalled_shouldReturnPageOfBookView() {
        // given
        int page = 0;
        int size = 10;
        BookView testBook = getTestBookView();
        Page<BookView> expectedResult = new PageImpl<>(List.of(testBook));

        // when
        when(bookRepository.getAllBy(any())).thenReturn(expectedResult);

        // then
        bookService.getAllBooks(page, size, Optional.empty(), Optional.empty());

        verify(bookRepository, times(1)).getAllBy(any());
    }

    @Test
    void testGetAllBooks_whenGetAllBooksCalledWithSortParams_shouldReturnPageOfBookView() {
        // given
        int page = 0;
        int size = 10;
        BookView testBook = getTestBookView();
        Page<BookView> expectedResult = new PageImpl<>(List.of(testBook));

        // when
        when(bookRepository.getAllBy(any())).thenReturn(expectedResult);

        // then
        bookService.getAllBooks(page, size, Optional.of("name"), Optional.of(Sort.Direction.ASC));

        verify(bookRepository, times(1)).getAllBy(any());
    }

    @Test
    void testMoveBook_whenMoveBookCalledWithNonExistingShelfId_shouldThrowEntityNotFoundException() {
        // given
        Long bookId = 1L;
        Long shelfId = 1L;
        Book testBook = getTestBook();

        // when
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(shelfService.findById(shelfId)).thenThrow(EntityNotFoundException.class);

        // then
        assertThatThrownBy(() -> bookService.moveBook(bookId, shelfId))
                .isInstanceOf(EntityNotFoundException.class);

        verify(bookRepository, times(1)).findById(bookId);
        verify(shelfService, times(1)).findById(shelfId);
    }


}