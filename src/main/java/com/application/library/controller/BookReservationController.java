package com.application.library.controller;


import com.application.library.data.dto.BookReservationRequestDto;
import com.application.library.data.view.BookReservationView;
import com.application.library.service.BookReservationService;
import com.application.library.utils.ErrorResponseHandler;
import com.application.library.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Book Reservation Controller", description = "Operations related to managing book reservations.")
@RequestMapping("/api/reservations")
public class BookReservationController {

    private final BookReservationService bookReservationService;

    public BookReservationController(BookReservationService bookReservationService) {
        this.bookReservationService = bookReservationService;
    }

    @Operation(
            summary = "Create a new reservation",
            description = "Create a new reservation for a book by providing the book ID and reservation details.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Reservation created successfully"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User already has a reservation",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User has an uncompleted reservation",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Book is not available for the selected date",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            }
    )
    @PostMapping("/{bookId}")
    public ResponseEntity<ResponseHandler<Long>> createNewReservation(@PathVariable Long bookId,
                                                                      @RequestBody BookReservationRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseHandler<>(bookReservationService.reserveBook(bookId, requestDto).getId()));
    }

    @Operation(
            summary = "Get all reservations for the authenticated user",
            description = "Retrieve all reservations for the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reservations retrieved successfully"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<ResponseHandler<Page<BookReservationView>>> getAuthenticUserReservations(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(new ResponseHandler<>(bookReservationService.getAuthenticUserReservations(page, size)));
    }

    @Operation(
            summary = "Update a reservation",
            description = "Update a reservation for a book by providing the reservation ID and new reservation details.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reservation updated successfully"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Reservation is already completed",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Book is not available for the selected date",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ResponseHandler<Long>> updateReservation(@PathVariable Long id, @RequestBody BookReservationRequestDto requestDto) {
        return ResponseEntity.ok(new ResponseHandler<>(bookReservationService.updateReservation(id, requestDto).getId()));
    }

    @Operation(
            summary = "Cancel a reservation",
            description = "Cancel a reservation for a book by providing the reservation ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reservation canceled successfully"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Reservation is already completed",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseHandler<Long>> cancelReservation(@PathVariable Long id) {
        return ResponseEntity.ok(new ResponseHandler<>(bookReservationService.cancelReservation(id).getId()));
    }
}
