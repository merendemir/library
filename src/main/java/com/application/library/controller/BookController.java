package com.application.library.controller;


import com.application.library.constants.MessageConstants;
import com.application.library.data.dto.CreateBookRequestDto;
import com.application.library.data.dto.SaveBookRequestDto;
import com.application.library.data.view.book.BookView;
import com.application.library.service.BookService;
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
@RequestMapping("/api/books")
@Tag(name = "Book Controller", description = "Operations related to managing books.")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Save a new book", description = "Save a new book. Requires ADMIN or LIBRARIAN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Book saved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Shelf is full. Cannot save book.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Book with this ISBN already exists",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            })
    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @PostMapping
    public ResponseEntity<ResponseHandler<Long>> saveBook(@RequestBody CreateBookRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(bookService.saveBook(requestDto).getId()));
    }

    @Operation(summary = "Get book by ID", description = "Retrieve book information by providing the book ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "book information retrieved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = MessageConstants.BOOK_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            })
    @GetMapping("{id}")
    public ResponseEntity<ResponseHandler<BookView>> getBook(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(bookService.getBookById(id)));
    }

    @Operation(summary = "Get all books", description = "Retrieve a paginated list of all books based on optional parameters.")
    @GetMapping
    public ResponseEntity<ResponseHandler<Page<BookView>>> getAllBooks(@RequestParam Optional<String> sortParam,
                                                                       @RequestParam Optional<Sort.Direction> direction,
                                                                       @RequestParam int page,
                                                                       @RequestParam int size) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(bookService.getAllBooks(page, size, sortParam, direction)));
    }

    @Operation(summary = "Delete book by ID", description = "Delete a book by providing the book ID. Requires ADMIN or LIBRARIAN role.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Book deleted successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = MessageConstants.BOOK_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
            )
    })
    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @DeleteMapping("{id}")
    public ResponseEntity<ResponseHandler<Long>> deleteBook(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(bookService.deleteBook(id)));
    }

    @Operation(summary = "Update book information by ID", description = "Update book information by providing the book ID and new details. Requires ADMIN or LIBRARIAN role.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Book updated successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = MessageConstants.BOOK_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
            )
    })
    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @PutMapping("/{id}")
    public ResponseEntity<ResponseHandler<Long>> updateBook(@PathVariable Long id, @RequestBody SaveBookRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(bookService.updateBook(id, requestDto).getId()));
    }

    @Operation(summary = "Move book to a different shelf", description = "Move a book to a different shelf by providing the book ID and target shelf ID. Requires ADMIN or LIBRARIAN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Book moved successfully"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Shelf is full. Cannot move book.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = MessageConstants.BOOK_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            })
    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @PutMapping("/move/{id}")
    public ResponseEntity<ResponseHandler<Long>> moveBook(@PathVariable Long id, @RequestParam Long shelfId) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(bookService.moveBook(id, shelfId).getId()));
    }
}
