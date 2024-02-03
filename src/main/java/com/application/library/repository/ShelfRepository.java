package com.application.library.repository;


import com.application.library.data.view.shelf.ShelfBaseView;
import com.application.library.data.view.shelf.ShelfView;
import com.application.library.model.Shelf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {

    boolean existsByName(String name);

    Optional<ShelfView> getShelfById(Long id);

    Page<ShelfBaseView> getAllBy(Pageable pageable);

}
