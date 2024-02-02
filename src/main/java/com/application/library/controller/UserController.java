package com.application.library.controller;


import com.application.library.data.dto.user.BaseUserSaveRequestDto;
import com.application.library.data.dto.user.UserSaveRequestDto;
import com.application.library.data.view.UserView;
import com.application.library.enumerations.UserRole;
import com.application.library.service.UserService;
import com.application.library.utils.ErrorResponseHandler;
import com.application.library.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "Operations related to managing users.")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Save a new user", description = "Save a new user with the provided information. Requires ADMIN or LIBRARIAN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User saved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User with this email already exists",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            })
    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @PostMapping
    public ResponseEntity<ResponseHandler<Long>> saveUser(@RequestBody UserSaveRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseHandler<>(userService.saveUser(requestDto).getId()));
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieve user information by providing the user ID. Requires ADMIN or LIBRARIAN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User information retrieved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))

                    )
            }
    )
    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @GetMapping("{id}")
    public ResponseEntity<ResponseHandler<UserView>> getUser(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(userService.getUserById(id)));
    }


    @Operation(summary = "Get all users", description = "Retrieve a paginated list of all users based on optional parameters. Requires ADMIN or LIBRARIAN role.")
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

    @Operation(
            summary = "Delete user by ID",
            description = "Delete a user by providing the user ID. Requires ADMIN or LIBRARIAN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "You are not authorized to delete librarian.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            }
    )
    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @DeleteMapping("{id}")
    public ResponseEntity<ResponseHandler<Long>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(userService.deleteUserByActiveUserAuthority(id)));
    }

    @Operation(summary = "Update user information by ID", description = "Update user information by providing the user ID and new details. Requires ADMIN or LIBRARIAN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User updated successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            })
    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @PutMapping("/{id}")
    public ResponseEntity<ResponseHandler<Long>> updateUser(@PathVariable Long id, @RequestBody UserSaveRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(userService.updateUser(id, requestDto).getId()));
    }

    @Operation(summary = "Update active user information", description = "Update information for the currently authenticated user. Requires ADMIN or LIBRARIAN role.")
    @PutMapping("/active")
    public ResponseEntity<ResponseHandler<Long>> updateActiveUserInfo(@RequestBody BaseUserSaveRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(userService.updateActiveUserInfo(requestDto).getId()));
    }
}
