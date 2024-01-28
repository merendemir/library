package com.application.library.controller;


import com.application.library.data.dto.SaveShelfRequestDto;
import com.application.library.data.view.ShelfBaseView;
import com.application.library.data.view.ShelfView;
import com.application.library.service.ShelfService;
import com.application.library.utils.ResponseHandler;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shelfs")
public class ShelfController {

    private final ShelfService shelfService;

    public ShelfController(ShelfService shelfService) {
        this.shelfService = shelfService;
    }

    @RolesAllowed({"ADMIN"})
    @PostMapping
    public ResponseEntity<ResponseHandler<Long>> saveShelf(@RequestBody SaveShelfRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(shelfService.saveShelf(requestDto).getId()));
    }

    @GetMapping("{id}")
    public ResponseEntity<ResponseHandler<ShelfView>> getShelf(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(shelfService.getShelfById(id)));
    }

    @GetMapping
    public ResponseEntity<ResponseHandler<Page<ShelfBaseView>>> getAllShelf(@RequestParam int page,
                                                                            @RequestParam int size) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(shelfService.getAllShelf(page, size)));
    }

    @RolesAllowed({"ADMIN"})
    @DeleteMapping("{id}")
    public ResponseEntity<ResponseHandler<Long>> deleteShelf(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(shelfService.deleteShelf(id)));
    }

    @RolesAllowed({"ADMIN"})
    @PutMapping("/{id}")
    public ResponseEntity<ResponseHandler<Long>> updateShelf(@PathVariable Long id, @RequestBody SaveShelfRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(shelfService.updateShelf(id, requestDto).getId()));
    }

}
