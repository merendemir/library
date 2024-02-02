package com.application.library.repository;


import com.application.library.data.view.BookView;
import com.application.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    Optional<BookView> getBookById(Long id);

    Page<BookView> getAllBy(Pageable pageable);
}
