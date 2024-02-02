package com.application.library.controller;


import com.application.library.data.dto.SaveShelfRequestDto;
import com.application.library.data.view.ShelfBaseView;
import com.application.library.data.view.ShelfView;
import com.application.library.service.ShelfService;
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

@Tag(name = "ShelfController", description = "Operations related to managing shelves.")
@RestController
@RequestMapping("/api/shelves")
public class ShelfController {

    private final ShelfService shelfService;

    public ShelfController(ShelfService shelfService) {
        this.shelfService = shelfService;
    }

    @Operation(summary = "Save a new shelf", description = "Save a new shelf. Requires ADMIN role.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Shelf saved successfully"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Shelf with this name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
            )
    })
    @RolesAllowed({"ADMIN"})
    @PostMapping
    public ResponseEntity<ResponseHandler<Long>> saveShelf(@RequestBody SaveShelfRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(shelfService.saveShelf(requestDto).getId()));
    }

    @Operation(summary = "Get shelf by ID", description = "Retrieve shelf information by providing the shelf ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Shelf information retrieved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Shelf not found.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            }
    )
    @GetMapping("{id}")
    public ResponseEntity<ResponseHandler<ShelfView>> getShelf(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(shelfService.getShelfById(id)));
    }

    @Operation(summary = "Get all shelves", description = "Retrieve a paginated list of all shelves.")
    @GetMapping
    public ResponseEntity<ResponseHandler<Page<ShelfBaseView>>> getAllShelves(@RequestParam int page,
                                                                              @RequestParam int size) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(shelfService.getAllShelf(page, size)));
    }

    @Operation(summary = "Delete shelf by ID", description = "Delete a shelf by providing the shelf ID. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Shelf deleted successfully"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Shelf not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Shelf is not empty. Cannot delete.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            }
    )
    @RolesAllowed({"ADMIN"})
    @DeleteMapping("{id}")
    public ResponseEntity<ResponseHandler<Long>> deleteShelf(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(shelfService.deleteShelf(id)));
    }

    @Operation(summary = "Update shelf information by ID", description = "Update shelf information by providing the shelf ID and new details. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Shelf updated successfully"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Shelf not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponseHandler.class))
                    )
            })
    @RolesAllowed({"ADMIN"})
    @PutMapping("/{id}")
    public ResponseEntity<ResponseHandler<Long>> updateShelf(@PathVariable Long id, @RequestBody SaveShelfRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(shelfService.updateShelf(id, requestDto).getId()));
    }
}
