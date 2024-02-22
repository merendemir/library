package com.application.library.controller;

import com.application.library.constants.MessageConstants;
import com.application.library.data.dto.CreateBookRequestDto;
import com.application.library.data.dto.SaveBookRequestDto;
import com.application.library.data.view.book.BookView;
import com.application.library.exception.EntityAlreadyExistsException;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.exception.ShelfFullException;
import com.application.library.exception.handler.DefaultExceptionHandler;
import com.application.library.model.Book;
import com.application.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookControllerTest extends BaseRestControllerTest {

    @MockBean
    private BookService bookService;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BookController(bookService))
                .setControllerAdvice(DefaultExceptionHandler.class)
                .build();
    }

    @Test
    void testSaveBook_whenSaveBookCalledWithSaveBookRequest_shouldReturnSavedBookId() throws Exception {
        // given
        Book testBook = getTestBook();
        CreateBookRequestDto saveBookRequestDto = new CreateBookRequestDto();

        when(bookService.saveBook(saveBookRequestDto)).thenReturn(testBook);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(saveBookRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data", is(testBook.getId().intValue())));
    }

    @Test
    void testSaveBook_whenSaveBookCalledWithFullShelf_shouldReturnHTTP400() throws Exception {
        // given
        CreateBookRequestDto saveBookRequestDto = new CreateBookRequestDto();
        String errorMessage = MessageConstants.SHELF_FULL;

        when(bookService.saveBook(saveBookRequestDto)).thenThrow(new ShelfFullException(errorMessage));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(saveBookRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testSaveBook_whenSaveBookCalledWithExistsISBN_shouldReturnHTTP409() throws Exception {
        // given
        CreateBookRequestDto saveBookRequestDto = new CreateBookRequestDto();
        saveBookRequestDto.setIsbn("1234567890");
        String errorMessage = MessageConstants.BOOK_ALREADY_EXISTS_WITH_ISBN;

        when(bookService.saveBook(saveBookRequestDto)).thenThrow(new EntityAlreadyExistsException(errorMessage));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(saveBookRequestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testGetBook_whenGetBookCalledWithId_shouldReturnBook() throws Exception {
        // given
        BookView testBookView = getTestBookView();
        Long bookId = testBookView.getId();
        when(bookService.getBookById(bookId)).thenReturn(testBookView);

        mockMvc.perform(get("/api/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(bookId.intValue())))
                .andExpect(jsonPath("$.data.name", is(testBookView.getName())))
                .andExpect(jsonPath("$.data.author", is(testBookView.getAuthor())))
                .andExpect(jsonPath("$.data.isbn", is(testBookView.getIsbn())))
                .andExpect(jsonPath("$.data.shelf.id", is(testBookView.getShelf().getId().intValue())))
                .andExpect(jsonPath("$.data.shelf.name", is(testBookView.getShelf().getName())))
                .andExpect(jsonPath("$.data.pageCount", is(testBookView.getPageCount())))
                .andExpect(jsonPath("$.data.publisher", is(testBookView.getPublisher())))
                .andExpect(jsonPath("$.data.publishedAt", is(testBookView.getPublishedAt())))
                .andExpect(jsonPath("$.data.language", is(testBookView.getLanguage())));
    }

    @Test
    void testGetBook_whenGetBookCalledWithNotExistsId_shouldReturnHTTP404() throws Exception {
        // given
        Long bookId = -1L;
        String errorMessage = MessageConstants.BOOK_NOT_FOUND;
        when(bookService.getBookById(bookId)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(get("/api/books/{id}", bookId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testGetAllBooks_whenGetAllBooksCalled_shouldReturnAllBooks() throws Exception {
        // given
        int page = 0;
        int size = 10;

        BookView testBookView = getTestBookView();
        Pageable pageable = PageRequest.of(page, size);
        List<BookView> bookViews = Collections.singletonList(testBookView);
        PageImpl<BookView> bookViewPage = new PageImpl<>(bookViews, pageable, bookViews.size());

        when(bookService.getAllBooks(page, size, Optional.empty(), Optional.empty())).thenReturn(bookViewPage);

        mockMvc.perform(get("/api/books?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id", is(testBookView.getId().intValue())))
                .andExpect(jsonPath("$.data.content[0].name", is(testBookView.getName())))
                .andExpect(jsonPath("$.data.content[0].author", is(testBookView.getAuthor())))
                .andExpect(jsonPath("$.data.content[0].isbn", is(testBookView.getIsbn())))
                .andExpect(jsonPath("$.data.content[0].shelf.id", is(testBookView.getShelf().getId().intValue())))
                .andExpect(jsonPath("$.data.content[0].shelf.name", is(testBookView.getShelf().getName())))
                .andExpect(jsonPath("$.data.content[0].pageCount", is(testBookView.getPageCount())))
                .andExpect(jsonPath("$.data.content[0].publisher", is(testBookView.getPublisher())))
                .andExpect(jsonPath("$.data.content[0].publishedAt", is(testBookView.getPublishedAt())))
                .andExpect(jsonPath("$.data.content[0].language", is(testBookView.getLanguage())));
    }

    @Test
    void testDeleteBook_whenDeleteBookCalledWithId_shouldReturnDeletedBookId() throws Exception {
        // given
        Long bookId = 1L;
        when(bookService.deleteBook(bookId)).thenReturn(bookId);

        mockMvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(bookId.intValue())));
    }

    @Test
    void testDeleteBook_whenDeleteBookCalledWithNotExistsId_shouldReturnHTTP404() throws Exception {
        // given
        Long bookId = -1L;
        String errorMessage = MessageConstants.BOOK_NOT_FOUND;
        when(bookService.deleteBook(bookId)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testUpdateBook_whenUpdateBookCalledWithIdAndSaveBookRequestDto_shouldReturnUpdatedBookId() throws Exception {
        // given
        Long bookId = 1L;
        SaveBookRequestDto saveBookRequestDto = new SaveBookRequestDto();
        Book testBook = getTestBook();
        when(bookService.updateBook(bookId, saveBookRequestDto)).thenReturn(testBook);

        mockMvc.perform(put("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(saveBookRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(bookId.intValue())));
    }

    @Test
    void testUpdateBook_whenUpdateBookCalledWithNotExistsId_shouldReturnHTTP404() throws Exception {
        // given
        Long bookId = -1L;
        SaveBookRequestDto saveBookRequestDto = new SaveBookRequestDto();
        String errorMessage = MessageConstants.BOOK_NOT_FOUND;
        when(bookService.updateBook(bookId, saveBookRequestDto)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(put("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(saveBookRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testUpdateBook_whenUpdateBookCalledWithTotalCountLessThanLendBookCount_shouldReturnHTTP400() throws Exception {
        // given
        Long bookId = 1L;
        SaveBookRequestDto saveBookRequestDto = new SaveBookRequestDto();
        String errorMessage = MessageConstants.BOOK_WILL_BE_LESS_THAN_LEND_BOOK_COUNT;
        when(bookService.updateBook(bookId, saveBookRequestDto)).thenThrow(new IllegalArgumentException(errorMessage));

        mockMvc.perform(put("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(saveBookRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testMoveBook_whenMoveBookCalledWithBookIdAndShelfId_shouldReturnMovedBookId() throws Exception {
        // given
        Book testBook = getTestBook();
        Long bookId = testBook.getId();
        Long shelfId = 1L;
        when(bookService.moveBook(bookId, shelfId)).thenReturn(testBook);

        mockMvc.perform(put("/api/books/move/{bookId}", bookId)
                        .param("shelfId", shelfId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(bookId.intValue())));
    }

    @Test
    void testMoveBook_whenMoveBookCalledWithFullShelf_shouldReturnHTTP400() throws Exception {
        // given
        Long bookId = 1L;
        Long shelfId = 1L;
        String errorMessage = MessageConstants.SHELF_FULL;
        when(bookService.moveBook(bookId, shelfId)).thenThrow(new ShelfFullException(errorMessage));

        mockMvc.perform(put("/api/books/move/{bookId}", bookId)
                        .param("shelfId", shelfId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testMoveBook_whenMoveBookCalledWithNotExistsBookId_shouldReturnHTTP404() throws Exception {
        // given
        Long bookId = -1L;
        Long shelfId = 1L;
        String errorMessage = MessageConstants.BOOK_NOT_FOUND;
        when(bookService.moveBook(bookId, shelfId)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(put("/api/books/move/{bookId}", bookId)
                        .param("shelfId", shelfId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testGetBooksByShelfId_whenGetBooksByShelfIdCalledWithShelfId_shouldReturnBooks() throws Exception {
        // given
        Long shelfId = 1L;

        // given
        int page = 0;
        int size = 10;

        BookView testBookView = getTestBookView();
        Pageable pageable = PageRequest.of(page, size);
        List<BookView> bookViews = Collections.singletonList(testBookView);
        PageImpl<BookView> bookViewPage = new PageImpl<>(bookViews, pageable, bookViews.size());
        when(bookService.findBooksByShelfId(shelfId, page, size)).thenReturn(bookViewPage);

        mockMvc.perform(get("/api/books/shelf/{shelfId}?page=0&size=10", shelfId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id", is(testBookView.getId().intValue())))
                .andExpect(jsonPath("$.data.content[0].name", is(testBookView.getName())))
                .andExpect(jsonPath("$.data.content[0].author", is(testBookView.getAuthor())))
                .andExpect(jsonPath("$.data.content[0].isbn", is(testBookView.getIsbn())))
                .andExpect(jsonPath("$.data.content[0].shelf.id", is(testBookView.getShelf().getId().intValue())))
                .andExpect(jsonPath("$.data.content[0].shelf.name", is(testBookView.getShelf().getName())))
                .andExpect(jsonPath("$.data.content[0].pageCount", is(testBookView.getPageCount())))
                .andExpect(jsonPath("$.data.content[0].publisher", is(testBookView.getPublisher())))
                .andExpect(jsonPath("$.data.content[0].publishedAt", is(testBookView.getPublishedAt())))
                .andExpect(jsonPath("$.data.content[0].language", is(testBookView.getLanguage())));
    }

}