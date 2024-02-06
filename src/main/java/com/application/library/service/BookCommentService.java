package com.application.library.service;

import com.application.library.converter.BookCommentConverter;
import com.application.library.data.dto.BookCommentDto;
import com.application.library.data.dto.BookCommentRequestDto;
import com.application.library.data.view.BookCommentStatsView;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.model.BookComment;
import com.application.library.repository.BookCommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookCommentService {

    private final BookCommentRepository bookCommentRepository;
    private final BookCommentConverter bookCommentConverter;

    public BookCommentService(BookCommentRepository bookCommentRepository, BookCommentConverter bookCommentConverter) {
        this.bookCommentRepository = bookCommentRepository;
        this.bookCommentConverter = bookCommentConverter;
    }

    @Transactional
    public BookComment saveCommentByBookId(Long bookId, BookCommentRequestDto requestDto) {
        return bookCommentRepository.save(bookCommentConverter.toEntity(bookId, requestDto));
    }

    @Transactional
    public BookComment updateComment(Long commentId, BookCommentRequestDto requestDto) {
        return bookCommentConverter.updateEntity(requestDto, findById(commentId));
    }

    @Transactional
    public Long deleteComment(Long commentId) {
        bookCommentRepository.delete(findById(commentId));
        return commentId;
    }


    @Transactional(readOnly = true)
    public Page<BookCommentDto> getCommentsByBookId(Long bookId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return bookCommentRepository.getCommentsByBookId(bookId, pageRequest);
    }

    @Transactional(readOnly = true)
    public BookCommentStatsView getBookCommentStats(Long bookId) {
        return bookCommentRepository.getBookCommentStats(bookId);
    }

    private BookComment findById(Long id) {
        return bookCommentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Book comment not found"));
    }

}
