package com.application.library.controller;

import com.application.library.constants.MessageConstants;
import com.application.library.data.dto.BookReservationRequestDto;
import com.application.library.data.view.BookReservationView;
import com.application.library.exception.EntityAlreadyExistsException;
import com.application.library.exception.handler.DefaultExceptionHandler;
import com.application.library.model.BookReservation;
import com.application.library.service.BookReservationService;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookReservationControllerTest extends BaseRestControllerTest {

    @MockBean
    private BookReservationService bookReservationService;

    @Autowired
    public MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BookReservationController(bookReservationService))
                .setControllerAdvice(DefaultExceptionHandler.class)
                .build();
    }

    @Test
    void testReserveBook_whenReserveBookCalledWithBookIdAndUserId_shouldReturnReservationId() throws Exception {
        BookReservation bookReservation = getTestBookReservation();
        BookReservationRequestDto requestDto = new BookReservationRequestDto();
        Long testReservationId = bookReservation.getId();
        Long testBookId = 1L;

        when(bookReservationService.reserveBook(testBookId, requestDto)).thenReturn(bookReservation);

        mockMvc.perform(post("/api/reservations/{bookId}", testBookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data", is(testReservationId.intValue())));
    }

    @Test
    void testReserveBook_whenReserveBookCalledWithExistingReservation_shouldReturnHTTP409() throws Exception {
        BookReservationRequestDto requestDto = new BookReservationRequestDto();
        Long testBookId = 1L;

        when(bookReservationService.reserveBook(testBookId, requestDto)).thenThrow(new EntityAlreadyExistsException(MessageConstants.USER_ALREADY_HAS_A_RESERVATION));

        mockMvc.perform(post("/api/reservations/{bookId}", testBookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testReserveBook_whenReserveBookCalledWithUncompletedReservation_shouldReturnHTTP409() throws Exception {
        BookReservationRequestDto requestDto = new BookReservationRequestDto();
        Long testBookId = 1L;

        when(bookReservationService.reserveBook(testBookId, requestDto)).thenThrow(new EntityAlreadyExistsException(MessageConstants.USER_HAS_AN_UNCOMPLETED_RESERVATION));

        mockMvc.perform(post("/api/reservations/{bookId}", testBookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testReserveBook_whenReserveBookCalledWithUnavailableBook_shouldReturnHTTP400() throws Exception {
        BookReservationRequestDto requestDto = new BookReservationRequestDto();
        Long testBookId = 1L;

        when(bookReservationService.reserveBook(testBookId, requestDto)).thenThrow(new IllegalStateException(MessageConstants.BOOK_IS_NOT_AVAILABLE_FOR_THE_SELECTED_DATE));

        mockMvc.perform(post("/api/reservations/{bookId}", testBookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testGetAuthenticationUserReservations_whenGetAuthenticationUserReservationsCalled_shouldReturnReservations() throws Exception {
        int page = 0;
        int size = 10;

        List<BookReservationView> bookReservationViewList = List.of(getBookReservationView());
        PageImpl<BookReservationView> bookReservationPage = new PageImpl<>(bookReservationViewList, PageRequest.of(page, size), bookReservationViewList.size());

        when(bookReservationService.getAuthenticationUserReservations(page, size)).thenReturn(bookReservationPage);

        mockMvc.perform(get("/api/reservations")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id", is(bookReservationViewList.get(0).getId().intValue())))
                .andExpect(jsonPath("$.data.content[0].book.id", is(bookReservationViewList.get(0).getBook().getId().intValue())));
    }

    @Test
    void testUpdateReservation_whenUpdateReservationCalledWithReservationIdAndNewReservationDetails_shouldReturnUpdatedReservationId() throws Exception {
        BookReservation bookReservation = getTestBookReservation();
        BookReservationRequestDto requestDto = new BookReservationRequestDto();
        Long testReservationId = bookReservation.getId();

        when(bookReservationService.updateReservation(testReservationId, requestDto)).thenReturn(bookReservation);

        mockMvc.perform(put("/api/reservations/{id}", testReservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(testReservationId.intValue())));
    }

    @Test
    void testUpdateReservation_whenUpdateReservationCalledWithCompletedReservation_shouldReturnHTTP409() throws Exception {
        BookReservationRequestDto requestDto = new BookReservationRequestDto();
        Long testReservationId = 1L;

        String errorMessage = MessageConstants.RESERVATION_ALREADY_COMPLETED;
        when(bookReservationService.updateReservation(testReservationId, requestDto)).thenThrow(new IllegalStateException(errorMessage));

        mockMvc.perform(put("/api/reservations/{id}", testReservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testUpdateReservation_whenUpdateReservationCalledWithUnavailableDate_shouldReturnHTTP409() throws Exception {
        BookReservationRequestDto requestDto = new BookReservationRequestDto();
        Long testReservationId = 1L;

        String errorMessage = MessageConstants.BOOK_IS_NOT_AVAILABLE_FOR_THE_SELECTED_DATE;
        when(bookReservationService.updateReservation(testReservationId, requestDto)).thenThrow(new IllegalStateException(errorMessage));

        mockMvc.perform(put("/api/reservations/{id}", testReservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testCancelReservation_whenCancelReservationCalledWithReservationId_shouldReturnCancelledReservationId() throws Exception {
        BookReservation bookReservation = getTestBookReservation();
        Long testReservationId = bookReservation.getId();

        when(bookReservationService.cancelReservation(testReservationId)).thenReturn(bookReservation);

        mockMvc.perform(delete("/api/reservations/{id}", testReservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(testReservationId.intValue())));
    }

    @Test
    void testCancelReservation_whenCancelReservationCalledWithCompletedReservation_shouldReturnHTTP409() throws Exception {
        Long testReservationId = 1L;

        String errorMessage = MessageConstants.RESERVATION_ALREADY_COMPLETED;
        when(bookReservationService.cancelReservation(testReservationId)).thenThrow(new IllegalStateException(errorMessage));

        mockMvc.perform(delete("/api/reservations/{id}", testReservationId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

}