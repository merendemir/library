package com.application.library.repository;


import com.application.library.data.view.UserView;
import com.application.library.enumerations.UserRole;
import com.application.library.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<UserView> getUserById(Long id);

    Page<UserView> getAllBy(Pageable pageable);

    Page<UserView> findAllByAuthorities(UserRole role, Pageable pageable);
}
