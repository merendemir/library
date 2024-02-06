package com.application.library.controller;


import com.application.library.data.dto.BookCommentDto;
import com.application.library.data.dto.BookCommentRequestDto;
import com.application.library.data.view.BookCommentStatsView;
import com.application.library.service.BookCommentService;
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
@RequestMapping("/api/comments")
@Tag(name = "Book Comment Controller", description = "Operations related to managing book comments.")
public class BookCommentController {

    private final BookCommentService bookCommentService;

    public BookCommentController(BookCommentService bookCommentService) {
        this.bookCommentService = bookCommentService;
    }


    @Operation(summary = "Save a new comment for a book", description = "Save a new comment for a book with the provided information.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Comment saved successfully"
                    )
            })
    @PostMapping("/book/{bookId}")
    public ResponseEntity<ResponseHandler<Long>> saveBookComments(@PathVariable Long bookId,
                                                                  @RequestBody BookCommentRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseHandler<>(bookCommentService.saveCommentByBookId(bookId, requestDto).getId()));
    }

    @Operation(
            summary = "Get comments by book ID",
            description = "Retrieve comments by providing the book ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comments retrieved successfully"
                    )
            })
    @GetMapping("/book/{bookId}")
    public ResponseEntity<ResponseHandler<Page<BookCommentDto>>> getCommentsByBookId(@PathVariable Long bookId,
                                                                                     @RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(bookCommentService.getCommentsByBookId(bookId, page, size)));
    }

    @Operation(
            summary = "Get comments stats by book ID",
            description = "Retrieve comments stats by providing the book ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comments stats retrieved successfully"
                    )
            })
    @GetMapping("/book/{bookId}/stats")
    public ResponseEntity<ResponseHandler<BookCommentStatsView>> getBookCommentStats(@PathVariable Long bookId) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(bookCommentService.getBookCommentStats(bookId)));
    }


    @Operation(
            summary = "Update a comment",
            description = "Update a comment by providing the comment ID and the new information.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comment updated successfully"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "You are not authorized to update this comment",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            })
    @PutMapping("/{commentId}")
    public ResponseEntity<ResponseHandler<Long>> updateComment(@PathVariable Long commentId,
                                                               @RequestBody BookCommentRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(bookCommentService.updateComment(commentId, requestDto).getId()));
    }

    @Operation(
            summary = "Delete a comment",
            description = "Delete a comment by providing the comment ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comment deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "You are not authorized to delete this comment",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseHandler<Long>> deleteComment(@PathVariable Long commentId) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(bookCommentService.deleteComment(commentId)));
    }


}
