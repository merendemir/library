package com.application.library.loader.data;

import com.application.library.enumerations.UserRole;
import com.application.library.model.User;
import com.application.library.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserLoader implements ApplicationRunner {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserLoader(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String adminMail = "system.admin";
        Optional<User> optionalAdmin = userRepository.findByEmail(adminMail);
        if (optionalAdmin.isPresent()) return;

        User admin = new User();
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setEmail(adminMail);
        admin.setPassword(bCryptPasswordEncoder.encode("123"));
        admin.setAuthorities(Set.of(UserRole.ROLE_ADMIN));
        userRepository.save(admin);
    }
}
