package com.application.library.controller;


import com.application.library.data.dto.user.BaseUserSaveRequestDto;
import com.application.library.data.dto.user.UserSaveRequestDto;
import com.application.library.data.view.UserView;
import com.application.library.enumerations.UserRole;
import com.application.library.service.UserService;
import com.application.library.utils.ResponseHandler;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @PostMapping
    public ResponseEntity<ResponseHandler<Long>> saveUser(@RequestBody UserSaveRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(userService.saveUser(requestDto).getId()));
    }

    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @GetMapping("{id}")
    public ResponseEntity<ResponseHandler<UserView>> getUser(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(userService.getUserById(id)));
    }

    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @GetMapping
    public ResponseEntity<ResponseHandler<Page<UserView>>> getAllUsers(@RequestParam Optional<UserRole> userType,
                                                                       @RequestParam Optional<String> sortParam,
                                                                       @RequestParam Optional<Sort.Direction> direction,
                                                                       @RequestParam int page,
                                                                       @RequestParam int size) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(
                userService.getAllUsersByActiveUserAuthority(userType, page, size, sortParam, direction)));
    }

    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @DeleteMapping("{id}")
    public ResponseEntity<ResponseHandler<Long>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(userService.deleteUserByActiveUserAuthority(id)));
    }

    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @PutMapping("/{id}")
    public ResponseEntity<ResponseHandler<Long>> updateUser(@PathVariable Long id, @RequestBody UserSaveRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(userService.updateUser(id, requestDto).getId()));
    }

    @PutMapping("/active")
    public ResponseEntity<ResponseHandler<Long>> updateActiveUserInfo(@RequestBody BaseUserSaveRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(userService.updateActiveUserInfo(requestDto).getId()));
    }

}
