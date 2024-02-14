package com.application.library.controller;

import com.application.library.constants.MessageConstants;
import com.application.library.data.dto.BookCommentDto;
import com.application.library.data.dto.BookCommentRequestDto;
import com.application.library.data.view.BookCommentStatsView;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.exception.handler.DefaultExceptionHandler;
import com.application.library.model.BookComment;
import com.application.library.service.BookCommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookCommentControllerTest extends BaseRestControllerTest {

    @MockBean
    private BookCommentService bookCommentService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BookCommentController(bookCommentService))
                .setControllerAdvice(DefaultExceptionHandler.class)
                .build();
    }

    @Test
    void testSaveBookComments_whenSaveBookCommentsCalledWithExistsBookIdAndBookCommentRequestDto_shouldReturnCommentId() throws Exception {
        // given
        BookCommentRequestDto bookCommentRequestDto = getBookCommentRequestDto();
        BookComment testBookComment = getTestBookComment();

        when(bookCommentService.saveCommentByBookId(testBookComment.getBook().getId(), bookCommentRequestDto)).thenReturn(testBookComment);

        mockMvc.perform(post("/api/comments//book/{bookId}", testBookComment.getBook().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookCommentRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data", is(testBookComment.getId().intValue())));
    }

    @Test
    void testSaveBookComments_whenSaveBookCommentsCalledWithNotExistsBookIdAndBookCommentRequestDto_shouldReturnHTTP404() throws Exception {
        // given
        Long bookId = -1L;
        BookCommentRequestDto bookCommentRequestDto = getBookCommentRequestDto();
        String errorMessage = MessageConstants.BOOK_NOT_FOUND;

        when(bookCommentService.saveCommentByBookId(bookId, bookCommentRequestDto)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(post("/api/comments//book/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookCommentRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testGetCommentsByBookId_whenGetCommentsByBookIdCalledWithExistsBookId_shouldReturnBookCommentPage() throws Exception {
        // given
        BookCommentDto bookCommentDto = getBookCommentDto();

        Long bookId = 1L;

        List<BookCommentDto> bookCommentDtoList = List.of(bookCommentDto);

        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<BookCommentDto> bookCommentDtoPage = new PageImpl<>(bookCommentDtoList, pageable, bookCommentDtoList.size());

        when(bookCommentService.getCommentsByBookId(bookId, 0, 10)).thenReturn(bookCommentDtoPage);

        mockMvc.perform(get("/api/comments//book/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.[0].id", is(bookCommentDto.getId().intValue())))
                .andExpect(jsonPath("$.data.content.[0].commentText", is(bookCommentDto.getCommentText())))
                .andExpect(jsonPath("$.data.content.[0].rating", is(bookCommentDto.getRating())))
                .andExpect(jsonPath("$.data.content.[0].user.firstName", is(bookCommentDto.getUser().getFirstName())))
                .andExpect(jsonPath("$.data.content.[0].user.lastName", is(bookCommentDto.getUser().getLastName())));
    }

    @Test
    void testGetBookCommentStats_whenGetBookCommentStatsCalledWithExistsBookId_shouldReturnBookCommentStatsView() throws Exception {
        // given
        Long bookId = 1L;
        BookCommentStatsView bookCommentStatsView = new BookCommentStatsView() {
            @Override
            public Long getTotalComments() {
                return 10L;
            }

            @Override
            public Double getAverageRating() {
                return 4.5;
            }
        };

        when(bookCommentService.getBookCommentStats(bookId)).thenReturn(bookCommentStatsView);

        mockMvc.perform(get("/api/comments//book/{bookId}/stats", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalComments", is(bookCommentStatsView.getTotalComments().intValue())))
                .andExpect(jsonPath("$.data.averageRating", is(bookCommentStatsView.getAverageRating())));
    }

    @Test
    void testUpdateComment_whenUpdateCommentCalledWithExistsCommentIdAndBookCommentRequestDto_shouldReturnCommentId() throws Exception {
        // given
        BookComment testBookComment = getTestBookComment();
        Long commentId = testBookComment.getId();
        BookCommentRequestDto bookCommentRequestDto = getBookCommentRequestDto();

        when(bookCommentService.updateComment(commentId, bookCommentRequestDto)).thenReturn(testBookComment);

        mockMvc.perform(put("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookCommentRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(testBookComment.getId().intValue())));
    }

    @Test
    void testUpdateComment_whenUpdateCommentCalledWithNotExistsCommentIdAndBookCommentRequestDto_shouldReturnHTTP404() throws Exception {
        // given
        Long commentId = -1L;
        BookCommentRequestDto bookCommentRequestDto = getBookCommentRequestDto();
        String errorMessage = MessageConstants.BOOK_COMMENT_NOT_FOUND;

        when(bookCommentService.updateComment(commentId, bookCommentRequestDto)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(put("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookCommentRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testUpdateComment_whenUpdateCommentCalledWithExistsCommentIdAndBookCommentRequestDtoAndUserIsNotAuthorized_shouldReturnForbidden() throws Exception {
        // given
        BookComment testBookComment = getTestBookComment();
        Long commentId = testBookComment.getId();
        BookCommentRequestDto bookCommentRequestDto = getBookCommentRequestDto();
        String errorMessage = MessageConstants.NOT_AUTHORIZED_FOR_UPDATE_COMMENT;

        when(bookCommentService.updateComment(commentId, bookCommentRequestDto)).thenThrow(new AccessDeniedException(errorMessage));

        mockMvc.perform(put("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookCommentRequestDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testDeleteComment_whenDeleteCommentCalledWithExistsCommentId_shouldReturnCommentId() throws Exception {
        // given
        BookComment testBookComment = getTestBookComment();
        Long commentId = testBookComment.getId();

        when(bookCommentService.deleteComment(commentId)).thenReturn(commentId);

        mockMvc.perform(delete("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(commentId.intValue())));
    }

    @Test
    void testDeleteComment_whenDeleteCommentCalledWithNotExistsCommentId_shouldReturnHTTP404() throws Exception {
        // given
        Long commentId = -1L;
        String errorMessage = MessageConstants.BOOK_COMMENT_NOT_FOUND;

        when(bookCommentService.deleteComment(commentId)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(delete("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testDeleteComment_whenDeleteCommentCalledWithExistsCommentIdAndUserIsNotAuthorized_shouldReturnHTTP403() throws Exception {
        // given
        BookComment testBookComment = getTestBookComment();
        Long commentId = testBookComment.getId();
        String errorMessage = MessageConstants.NOT_AUTHORIZED_FOR_DELETE_COMMENT;

        when(bookCommentService.deleteComment(commentId)).thenThrow(new AccessDeniedException(errorMessage));

        mockMvc.perform(delete("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

}