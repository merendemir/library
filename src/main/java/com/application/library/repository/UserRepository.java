package com.application.library.repository;


import com.application.library.data.view.UserView;
import com.application.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<UserView> getUserById(Long id);
}
