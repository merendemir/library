package com.application.library.repository;


import com.application.library.data.view.LendTransactionAuthUserView;
import com.application.library.data.view.LendTransactionView;
import com.application.library.model.LendTransaction;
import com.application.library.model.Shelf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LendTransactionRepository extends JpaRepository<LendTransaction, UUID> {

    boolean existsByUser_IdAndReturnedFalse(Long userId);

    int countAllByBook_IdAndReturnedFalse(Long bookId);

    Page<LendTransactionAuthUserView> findAllByUser_Id(Long userId, Pageable pageable);

    Page<LendTransactionView> findAllByReturned(boolean returned, Pageable pageable);
}
