package com.application.library.repository;


import com.application.library.data.dto.BookCommentDto;
import com.application.library.data.view.BookCommentStatsView;
import com.application.library.model.BookComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookCommentRepository extends JpaRepository<BookComment, Long> {

    @Query("SELECT NEW com.application.library.data.dto.BookCommentDto(bc.id, bc.createdAt, bc.updatedAt, bc.commentText, bc.rating, bc.user.firstName, bc.user.lastName) " +
            "FROM book_comment bc " +
            "WHERE bc.book.id = :bookId")
    Page<BookCommentDto> getCommentsByBookId(Long bookId, Pageable pageable);


    @Query("SELECT COUNT(bc.id) as totalComments, AVG(bc.rating) as averageRating " +
            "FROM book_comment bc " +
            "WHERE bc.book.id = :bookId")
    BookCommentStatsView getBookCommentStats(Long bookId);

}
