package com.application.library.controller;


import com.application.library.constants.MessageConstants;
import com.application.library.data.view.ReadingListView;
import com.application.library.service.ReadingListService;
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
@RequestMapping("/api/reading/list")
@Tag(name = "Reading List Controller", description = "Operations related to managing reading list.")
public class ReadingListController {

    private final ReadingListService readingListService;

    public ReadingListController(ReadingListService readingListService) {
        this.readingListService = readingListService;
    }

    @Operation(
            summary = "Add a book to reading list",
            description = "Add a book to the reading list by providing the book ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Book added to reading list successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = MessageConstants.BOOK_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            })
    @PostMapping("/book/{bookId}")
    public ResponseEntity<ResponseHandler<Long>> addBookToReadingList(@PathVariable Long bookId) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(readingListService.addBookToReadingList(bookId).getId()));
    }

    @Operation(
            summary = "Remove a book from reading list",
            description = "Remove a book from the reading list by providing the book ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Book removed from reading list successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = MessageConstants.BOOK_NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            })
    @DeleteMapping("/book/{bookId}")
    public ResponseEntity<ResponseHandler<Long>> removeBookFromReadingList(@PathVariable Long bookId) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(readingListService.removeBookFromReadingList(bookId).getId()));
    }

    @Operation(
            summary = "Get reading list",
            description = "Retrieve reading list.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reading list retrieved successfully"
                    )
            })
    @GetMapping
    public ResponseEntity<ResponseHandler<Page<ReadingListView>>> getReadingList(@RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(readingListService.getReadingList(page, size)));
    }

    @Operation(
            summary = "Clear reading list",
            description = "Clear reading list.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reading list cleared successfully"
                    )
            })
    @DeleteMapping
    public ResponseEntity<ResponseHandler<Long>> clearReadingList() {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(readingListService.clearReadingList()));
    }

}
