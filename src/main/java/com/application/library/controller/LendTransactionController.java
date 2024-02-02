package com.application.library.controller;


import com.application.library.data.dto.LendTransactionRequestDto;
import com.application.library.data.view.LendTransactionAuthUserView;
import com.application.library.data.view.LendTransactionView;
import com.application.library.service.LendTransactionService;
import com.application.library.utils.ErrorResponseHandler;
import com.application.library.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Lend Transaction Controller", description = "Operations related to managing lend transactions.")
@RestController
@RequestMapping("/api/lend/transactions")
public class LendTransactionController {

    private final LendTransactionService lendTransactionService;

    public LendTransactionController(LendTransactionService lendTransactionService) {
        this.lendTransactionService = lendTransactionService;
    }

    @Operation(summary = "Save a new lend transaction", description = "Save a new lend transaction. Requires LIBRARIAN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lend transaction saved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Book is not available for lending",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
                    ,
                    @ApiResponse(
                            responseCode = "400",
                            description = "User has already lent a book",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            })
    @RolesAllowed({"LIBRARIAN"})
    @PostMapping
    public ResponseEntity<ResponseHandler<UUID>> lendBook(@RequestBody LendTransactionRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(lendTransactionService.lendBook(requestDto).getId()));
    }


    @Operation(summary = "Return a lend transaction", description = "Return a lend transaction. Requires LIBRARIAN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lend transaction returned successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "You have to pay late fee",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Book is already returned",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            })
    @RolesAllowed({"LIBRARIAN"})
    @PutMapping("/{id}/return")
    public ResponseEntity<ResponseHandler<UUID>> returnBook(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(lendTransactionService.returnBook(id).getId()));
    }


    @Operation(summary = "Pay late fee", description = "Pay late fee for a lend transaction. Requires LIBRARIAN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Late fee paid successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "No late fee to pay",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            })
    @RolesAllowed({"LIBRARIAN"})
    @PutMapping("/{id}/pay/late/fee")
    public ResponseEntity<ResponseHandler<UUID>> payLateFee(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(lendTransactionService.payLateFee(id).getId()));
    }

    @Operation(summary = "Get late fee by lend transaction id", description = "Get late fee by lend transaction id")
    @GetMapping("/{id}/pay/late/fee")
    public ResponseEntity<ResponseHandler<Double>> getPayLateFeeById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(lendTransactionService.getLateFeeById(id)));
    }

    @Operation(summary = "Find lend transactions for authenticated user", description = "Find lend transactions for authenticated user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lend transactions found successfully"
                    )
            })
    @GetMapping("/user")
    public ResponseEntity<ResponseHandler<Page<LendTransactionAuthUserView>>> findLendTransactionsForAuthUser(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(lendTransactionService.findLendTransactionsForAuthUser(page, size)));
    }

    @Operation(summary = "Find lend transactions by user id", description = "Find lend transactions by user id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lend transactions found successfully"
                    )
            })
    @GetMapping("/user/{id}")
    public ResponseEntity<ResponseHandler<Page<LendTransactionAuthUserView>>> findLendTransactionsByUserId(@PathVariable Long id, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(lendTransactionService.findLendTransactionsByUserId(id, page, size)));
    }

    @Operation(summary = "Find lend transactions by returned", description = "Find lend transactions by returned",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lend transactions found successfully"
                    )
            })
    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @GetMapping("/returned/{returned}")
    public ResponseEntity<ResponseHandler<Page<LendTransactionView>>> findLendTransactionsByReturned(@PathVariable boolean returned, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(lendTransactionService.findLendTransactionsByReturned(returned, page, size)));
    }
}
