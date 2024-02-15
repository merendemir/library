package com.application.library.controller;

import com.application.library.constants.MessageConstants;
import com.application.library.data.view.ReadingListView;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.exception.handler.DefaultExceptionHandler;
import com.application.library.model.ReadingList;
import com.application.library.service.ReadingListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReadingListControllerTest extends BaseRestControllerTest {

    @MockBean
    private ReadingListService readingListService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ReadingListController(readingListService))
                .setControllerAdvice(DefaultExceptionHandler.class)
                .build();
    }

    @Test
    void testAddBookToReadingList_whenAddBookToListCalledWithExistsBookId_shouldReturnReadingListId() throws Exception {
        // given
        Long bookId = 1L;
        ReadingList testReadingList = getTestReadingList();
        when(readingListService.addBookToReadingList(bookId)).thenReturn(testReadingList);

        mockMvc.perform(post("/api/reading/list/book/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(testReadingList.getId().intValue())));
    }

    @Test
    void testAddBookToReadingList_whenAddBookToListCalledWithNotExistsBookId_shouldReturnHTTP404() throws Exception {
        // given
        Long bookId = 1L;
        String errorMessage = MessageConstants.BOOK_NOT_FOUND;
        when(readingListService.addBookToReadingList(bookId)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(post("/api/reading/list/book/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testRemoveBookFromReadingList_whenRemoveBookFromListCalledWithExistsBookId_shouldReturnReadingListId() throws Exception {
        // given
        Long bookId = 1L;
        ReadingList testReadingList = getTestReadingList();
        when(readingListService.removeBookFromReadingList(bookId)).thenReturn(testReadingList);

        mockMvc.perform(delete("/api/reading/list/book/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(testReadingList.getId().intValue())));
    }

    @Test
    void testRemoveBookFromReadingList_whenRemoveBookFromListCalledWithNotExistsBookId_shouldReturnHTTP404() throws Exception {
        // given
        Long bookId = 1L;
        String errorMessage = MessageConstants.BOOK_NOT_FOUND;
        when(readingListService.removeBookFromReadingList(bookId)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(delete("/api/reading/list/book/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testGetReadingList_whenGetReadingListCalled_shouldReturnReadingList() throws Exception {
        // given
        int page = 0;
        int size = 10;
        List<ReadingListView> readingListView = List.of(getReadingListView());
        PageImpl<ReadingListView> readingListViewPage = new PageImpl<>(readingListView, PageRequest.of(page, size), readingListView.size());
        when(readingListService.getReadingList(page, size)).thenReturn(readingListViewPage);

        mockMvc.perform(get("/api/reading/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id", is(readingListView.get(0).getId().intValue())));
    }

    @Test
    void testClearReadingList_whenClearReadingListCalled_shouldReturnHTTP200() throws Exception {
        // given
        mockMvc.perform(delete("/api/reading/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}