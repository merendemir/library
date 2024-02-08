package com.application.library.service;

import com.application.library.converter.BookCommentConverter;
import com.application.library.data.dto.BookCommentDto;
import com.application.library.data.dto.BookCommentRequestDto;
import com.application.library.data.view.BookCommentStatsView;
import com.application.library.helper.AuthHelper;
import com.application.library.model.BookComment;
import com.application.library.repository.BookCommentRepository;
import com.application.library.support.TestSupport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BookCommentServiceTest extends TestSupport {

    private BookCommentRepository bookCommentRepository;
    private BookCommentConverter bookCommentConverter;
    private BookCommentService bookCommentService;

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
        bookCommentRepository = mock(BookCommentRepository.class);
        bookCommentConverter = mock(BookCommentConverter.class);
        bookCommentService = new BookCommentService(bookCommentRepository, bookCommentConverter);
    }

    @Test
    void testSaveCommentByBookId_whenTestSaveCommentCalledWithBookIdAndBookCommentRequestDto_shouldReturnBookComment() {
        // given
        BookCommentRequestDto bookCommentRequestDto = getBookCommentRequestDto();
        BookComment testBookComment = getTestBookComment();
        Long bookId = testBookComment.getBook().getId();

        // when
        when(bookCommentConverter.toEntity(bookId, bookCommentRequestDto)).thenReturn(testBookComment);
        when(bookCommentRepository.save(testBookComment)).thenReturn(testBookComment);

        // then
        BookComment result = bookCommentService.saveCommentByBookId(bookId, bookCommentRequestDto);

        assertEquals(testBookComment, result);

        verify(bookCommentConverter, times(1)).toEntity(bookId, bookCommentRequestDto);
        verify(bookCommentRepository, times(1)).save(testBookComment);
    }

    @Test
    void testUpdateComment_whenTestUpdateCommentCalledWithCommentIdAndBookCommentRequestDto_shouldReturnBookComment() {
        // given
        BookCommentRequestDto bookCommentRequestDto = getBookCommentRequestDto();
        BookComment testBookComment = getTestBookComment();
        Long commentId = testBookComment.getId();

        // when
        when(AuthHelper.getActiveUser()).thenReturn(getTestUser());
        when(bookCommentRepository.findById(commentId)).thenReturn(Optional.of(testBookComment));
        when(bookCommentConverter.updateEntity(bookCommentRequestDto, testBookComment)).thenReturn(testBookComment);

        // then
        BookComment result = bookCommentService.updateComment(commentId, bookCommentRequestDto);

        assertEquals(testBookComment, result);

        verify(bookCommentRepository, times(1)).findById(commentId);
        verify(bookCommentConverter, times(1)).updateEntity(bookCommentRequestDto, testBookComment);
    }

    @Test
    void testUpdateComment_whenTestUpdateCommentCalledWithCommentIdAndBookCommentRequestDtoDifferentUser_shouldThrowAccessDeniedException() {
        // given
        BookCommentRequestDto bookCommentRequestDto = getBookCommentRequestDto();
        BookComment testBookComment = getTestBookComment();

        Long commentId = testBookComment.getId();

        // when
        when(AuthHelper.getActiveUser()).thenReturn(getTestUser2());
        when(bookCommentRepository.findById(commentId)).thenReturn(Optional.of(testBookComment));

        // then
        assertThrows(AccessDeniedException.class, () -> bookCommentService.updateComment(commentId, bookCommentRequestDto));

        verify(bookCommentRepository, times(1)).findById(commentId);
        verifyNoInteractions(bookCommentConverter);
    }

    @Test
    void testDeleteComment_whenTestDeleteCommentCalledWithCommentId_shouldReturnCommentId() {
        // given
        BookComment testBookComment = getTestBookComment();
        Long commentId = testBookComment.getId();

        // when
        when(AuthHelper.getActiveUser()).thenReturn(getTestUser());
        when(bookCommentRepository.findById(commentId)).thenReturn(Optional.of(testBookComment));

        // then
        Long result = bookCommentService.deleteComment(commentId);

        assertEquals(commentId, result);

        verify(bookCommentRepository, times(1)).findById(commentId);
        verify(bookCommentRepository, times(1)).delete(testBookComment);
    }

    @Test
    void testDeleteComment_whenTestDeleteCommentCalledWithCommentIdDifferentUser_shouldThrowAccessDeniedException() {
        // given
        BookComment testBookComment = getTestBookComment();
        Long commentId = testBookComment.getId();

        // when
        when(AuthHelper.getActiveUser()).thenReturn(getTestUser2());
        when(bookCommentRepository.findById(commentId)).thenReturn(Optional.of(testBookComment));

        // then
        assertThrows(AccessDeniedException.class, () -> bookCommentService.deleteComment(commentId));

        verify(bookCommentRepository, times(1)).findById(commentId);
        verifyNoMoreInteractions(bookCommentRepository);
    }

    @Test
    void testGetCommentsByBookId_whenTestGetCommentsByBookIdCalledWithBookIdAndPageAndSize_shouldReturnPageOfBookCommentDto() {
        // given
        Long bookId = 1L;
        int page = 0;
        int size = 10;
        BookCommentDto bookCommentDto = new BookCommentDto();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageImpl<BookCommentDto> bookCommentDtoPage = new PageImpl<>(List.of(bookCommentDto));

        // when
        when(bookCommentRepository.getCommentsByBookId(bookId, pageRequest)).thenReturn(bookCommentDtoPage);

        // then
        bookCommentService.getCommentsByBookId(bookId, page, size);

        verify(bookCommentRepository, times(1)).getCommentsByBookId(bookId, pageRequest);
    }

    @Test
    void testGetBookCommentStats_whenTestGetBookCommentStatsCalledWithBookId_shouldReturnBookCommentStatsView() {
        // given
        Long bookId = 1L;
        BookCommentStatsView bookCommentStatsView = new BookCommentStatsView() {
            @Override
            public Long getTotalComments() {
                return 5L;
            }

            @Override
            public Double getAverageRating() {
                return 5.0;
            }
        };

        // when
        when(bookCommentRepository.getBookCommentStats(bookId)).thenReturn(bookCommentStatsView);

        // then
        BookCommentStatsView bookCommentStats = bookCommentService.getBookCommentStats(bookId);

        assertEquals(bookCommentStatsView, bookCommentStats);

        verify(bookCommentRepository, times(1)).getBookCommentStats(bookId);
    }
}