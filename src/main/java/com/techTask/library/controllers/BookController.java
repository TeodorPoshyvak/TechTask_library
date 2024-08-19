package com.techTask.library.controllers;

import com.techTask.library.data.request.book.CreateBookRequest;
import com.techTask.library.data.request.book.UpdateBookRequest;
import com.techTask.library.data.response.BookResponse;
import com.techTask.library.data.response.ErrorResponse;
import com.techTask.library.exception.BookDeleteBorrowedException;
import com.techTask.library.services.interfaces.BookService;
import com.techTask.library.utils.BookMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@Tag(name = "books_methods")
public class BookController {


    private final BookService bookService;

    private final BookMapper bookMapper;

    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        return ResponseEntity.ok(bookMapper.toBookResponses(bookService.getAllBooks()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(bookMapper.toBookResponse(bookService.getBookById(id)));
        } catch (EntityNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@NotNull @ModelAttribute CreateBookRequest bookRequest) {
        return ResponseEntity.ok(
                bookMapper.toBookResponse(
                        bookService.createBook(
                                bookMapper.toBookDTO(bookRequest))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id,
                                        @RequestParam(required = false) String title,
                                        @RequestParam(required = false) String author) {
        try {
            UpdateBookRequest updateBookRequest = new UpdateBookRequest(title, author);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(bookMapper.toBookResponse(
                            bookService.updateBook(id, bookMapper.toBookDTO(updateBookRequest))));
        } catch (EntityNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (EntityNotFoundException | BookDeleteBorrowedException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

    }
}
