package com.application.library.controller;


import com.application.library.data.dto.CreateBookRequestDto;
import com.application.library.data.dto.SaveBookRequestDto;
import com.application.library.data.view.BookView;
import com.application.library.service.BookService;
import com.application.library.utils.ResponseHandler;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @PostMapping
    public ResponseEntity<ResponseHandler<Long>> saveBook(@RequestBody CreateBookRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(bookService.saveBook(requestDto).getId()));
    }

    @GetMapping("{id}")
    public ResponseEntity<ResponseHandler<BookView>> getBook(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(bookService.getBookById(id)));
    }

    @GetMapping
    public ResponseEntity<ResponseHandler<Page<BookView>>> getAllBooks(@RequestParam Optional<String> sortParam,
                                                                       @RequestParam Optional<Sort.Direction> direction,
                                                                       @RequestParam int page,
                                                                       @RequestParam int size) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(
                bookService.getAllBooks(page, size, sortParam, direction)));
    }

    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @DeleteMapping("{id}")
    public ResponseEntity<ResponseHandler<Long>> deleteBook(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(bookService.deleteBook(id)));
    }

    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @PutMapping("/{id}")
    public ResponseEntity<ResponseHandler<Long>> updateBook(@PathVariable Long id, @RequestBody SaveBookRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(bookService.updateBook(id, requestDto).getId()));
    }

    @RolesAllowed({"ADMIN", "LIBRARIAN"})
    @PutMapping("/move/{id}")
    public ResponseEntity<ResponseHandler<Long>> moveBook(@PathVariable Long id, @RequestParam Long shelfId) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(bookService.moveBook(id, shelfId).getId()));
    }

}
