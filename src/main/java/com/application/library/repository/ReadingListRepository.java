package com.application.library.repository;


import com.application.library.data.view.ReadingListView;
import com.application.library.model.ReadingList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReadingListRepository extends JpaRepository<ReadingList, Long> {

    Optional<ReadingList> findByUser_Id(Long userId);

    Page<ReadingListView> findByUser_Id(Long userId, Pageable pageable);
}
