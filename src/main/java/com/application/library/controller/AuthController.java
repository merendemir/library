package com.application.library.controller;


import com.application.library.data.dto.BaseUserSaveRequestDto;
import com.application.library.data.dto.LoginRequestDto;
import com.application.library.service.AuthService;
import com.application.library.service.UserService;
import com.application.library.utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseHandler<Long>> register(@RequestBody BaseUserSaveRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(userService.saveBaseUser(requestDto).getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseHandler<String>> login(@RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(authService.login(requestDto)));
    }

}
