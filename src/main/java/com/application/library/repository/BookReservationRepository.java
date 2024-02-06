package com.application.library.repository;


import com.application.library.data.view.BookReservationView;
import com.application.library.data.view.ReadingListView;
import com.application.library.model.BookReservation;
import com.application.library.model.ReadingList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface BookReservationRepository extends JpaRepository<BookReservation, Long> {

    Page<BookReservationView> findByUser_Id(Long userId, Pageable pageable);

    boolean existsByUser_IdAndReservationDateAfter(Long userId, LocalDate date);
    boolean existsByUser_IdAndCompletedAndCreatedAtAfter(Long userId, boolean completed, LocalDateTime date);

    long countByBook_IdAndReservationDateAfterAndCompletedFalse(Long bookId, LocalDate date);

    long countByBook_IdAndReservationDateBeforeAndCompletedFalse(Long bookId, LocalDate date);

    Optional<BookReservation> findByBook_IdAndUser_IdAndCompletedFalse(Long bookId, Long userId);
}
