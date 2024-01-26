package com.application.library.controller;


import com.application.library.data.dto.UserSaveRequestDto;
import com.application.library.data.view.UserView;
import com.application.library.service.UserService;
import com.application.library.utils.ResponseHandler;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
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

}
