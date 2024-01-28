package com.application.library.repository;


import com.application.library.data.view.ShelfBaseView;
import com.application.library.data.view.ShelfView;
import com.application.library.model.Shelf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {

    Optional<ShelfView> getShelfById(Long id);

    Page<ShelfBaseView> getAllBy(Pageable pageable);

}
