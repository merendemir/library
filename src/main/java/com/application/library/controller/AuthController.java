package com.application.library.controller;


import com.application.library.data.dto.user.BaseUserSaveRequestDto;
import com.application.library.data.dto.user.LoginRequestDto;
import com.application.library.data.dto.user.LoginResponseDto;
import com.application.library.service.AuthService;
import com.application.library.service.UserService;
import com.application.library.utils.ErrorResponseHandler;
import com.application.library.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth Controller", description = "Controller for authentication-related operations, including user registration and login.")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @Operation(summary = "Register a new user and retrieve the user ID", description = "Registers a new user using the provided information and returns the user ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User registered successfully"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User with this email already exists",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            })
    @PostMapping("/register")
    public ResponseEntity<ResponseHandler<Long>> register(@RequestBody BaseUserSaveRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseHandler<>(userService.saveBaseUser(requestDto).getId()));
    }


    @Operation(summary = "Login with user credentials", description = "Authenticate and login the user using the provided credentials.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User logged in successfully"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid credentials",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            })
    @PostMapping("/login")
    public ResponseEntity<ResponseHandler<LoginResponseDto>> login(@RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(authService.login(requestDto)));
    }

}
