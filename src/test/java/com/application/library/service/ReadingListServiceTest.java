package com.application.library.service;

import com.application.library.data.view.ReadingListView;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.helper.AuthHelper;
import com.application.library.model.Book;
import com.application.library.model.ReadingList;
import com.application.library.model.User;
import com.application.library.repository.ReadingListRepository;
import com.application.library.support.TestSupport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class ReadingListServiceTest extends TestSupport {

    private ReadingListService readingListService;
    private ReadingListRepository readingListRepository;
    private BookService bookService;

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
        readingListRepository = mock(ReadingListRepository.class);
        bookService = mock(BookService.class);
        readingListService = new ReadingListService(readingListRepository, bookService);
    }


    @Test
    void testAddBookToReadingList_whenAddBookToReadingListValidBookIdAndReadingListExists_shouldReturnReadingList() {
        // given
        Long bookId = 1L;
        Book testBook = getTestBook();
        User testUser = getTestUser();
        ReadingList testReadingList = getTestReadingList();

        // when
        when(AuthHelper.getActiveUser()).thenReturn(testUser);
        when(bookService.findById(bookId)).thenReturn(testBook);
        when(readingListRepository.findByUser_Id(any())).thenReturn(Optional.of(testReadingList));
        when(readingListRepository.save(any(ReadingList.class))).thenReturn(testReadingList);

        // then
        ReadingList actual = readingListService.addBookToReadingList(bookId);

        assertEquals(testReadingList, actual);

        verify(bookService, times(1)).findById(any());
        verify(readingListRepository, times(1)).findByUser_Id(any());
        verify(readingListRepository, times(1)).save(any(ReadingList.class));
    }

    @Test
    void testAddBookToReadingList_whenAddBookToReadingListValidBookIdAndReadingListNotExists_shouldReturnReadingList() {
        // given
        Book testBook = getTestBook();
        Long bookId = testBook.getId();
        User testUser = getTestUser();
        ReadingList testReadingList = getTestReadingList();

        // when
        when(AuthHelper.getActiveUser()).thenReturn(testUser);
        when(bookService.findById(bookId)).thenReturn(testBook);
        when(readingListRepository.findByUser_Id(any())).thenReturn(Optional.empty());
        when(readingListRepository.save(any(ReadingList.class))).thenReturn(testReadingList);

        // then
        ReadingList actual = readingListService.addBookToReadingList(bookId);

        assertEquals(testReadingList, actual);

        verify(bookService, times(1)).findById(bookId);
        verify(readingListRepository, times(1)).findByUser_Id(any());
        verify(readingListRepository, times(1)).save(any(ReadingList.class));
    }

    @Test
    void testRemoveBookFromReadingList_whenRemoveBookFromReadingListValidBookIdAndReadingListExists_shouldReturnReadingList() {
        // given
        Long bookId = 1L;
        Book testBook = getTestBook();
        User testUser = getTestUser();
        ReadingList testReadingList = getTestReadingList();

        // when
        when(AuthHelper.getActiveUser()).thenReturn(testUser);
        when(bookService.findById(bookId)).thenReturn(testBook);
        when(readingListRepository.findByUser_Id(any())).thenReturn(Optional.of(testReadingList));
        when(readingListRepository.save(any(ReadingList.class))).thenReturn(testReadingList);

        // then
        ReadingList actual = readingListService.removeBookFromReadingList(1L);

        assertEquals(testReadingList, actual);

        verify(bookService, times(1)).findById(any());
        verify(readingListRepository, times(1)).findByUser_Id(any());
        verify(readingListRepository, times(1)).save(any(ReadingList.class));
    }

    @Test
    void testRemoveBookFromReadingList_whenRemoveBookFromReadingListValidBookIdAndReadingListNotExists_shouldThrowEntityNotFoundException() {
        // given
        Long bookId = 1L;
        Book testBook = getTestBook();
        User testUser = getTestUser();

        // when
        when(AuthHelper.getActiveUser()).thenReturn(testUser);
        when(bookService.findById(bookId)).thenReturn(testBook);
        when(readingListRepository.findByUser_Id(any())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> readingListService.removeBookFromReadingList(bookId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Reading list not found");

        verify(bookService, times(1)).findById(any());
        verify(readingListRepository, times(1)).findByUser_Id(any());
        verify(readingListRepository, never()).save(any(ReadingList.class));
    }

    @Test
    void testGetReadingList_whenGetReadingListValidPageAndSize_shouldReturnReadingList() {
        // given
        ReadingListView testReadingList = getReadingListView();
        PageImpl<ReadingListView> expectedResult = new PageImpl<>(List.of(testReadingList));

        // when
        when(AuthHelper.getActiveUser()).thenReturn(getTestUser());
        when(readingListRepository.findByUser_Id(any(), any())).thenReturn(expectedResult);

        // then
        Page<ReadingListView> actual = readingListService.getReadingList(0, 10);

        assertEquals(expectedResult, actual);

        verify(readingListRepository, times(1)).findByUser_Id(any(), any());
    }

    @Test
    void testClearReadingList_whenClearReadingListValid_shouldReturnUserId() {
        // given
        User testUser = getTestUser();

        // when
        when(AuthHelper.getActiveUser()).thenReturn(testUser);
        when(readingListRepository.findByUser_Id(any())).thenReturn(Optional.of(getTestReadingList()));

        // then
        Long actual = readingListService.clearReadingList();

        assertEquals(testUser.getId(), actual);

        verify(readingListRepository, times(1)).findByUser_Id(any());
        verify(readingListRepository, times(1)).save(any(ReadingList.class));
    }

}