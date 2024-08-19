package com.techTask.library.controllers;

import com.techTask.library.data.dto.BookMemberDTO;
import com.techTask.library.data.response.ErrorResponse;
import com.techTask.library.exception.BookBorrowEmptyException;
import com.techTask.library.exception.MemberBorrowException;
import com.techTask.library.services.interfaces.BorrowService;
import com.techTask.library.utils.MemberMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/borrowing")
@Tag(name = "borrow_methods")
public class BorrowController {
    @Autowired
    private BorrowService borrowService;
    @Autowired
    private MemberMapper memberMapper;


    @PostMapping("/member/{memberId}/borrow/{bookId}")
    public ResponseEntity<?> borrowBook(@PathVariable Long memberId, @PathVariable Long bookId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    memberMapper.toMemberResponse(borrowService.borrowBook(bookId, memberId)));
        } catch (EntityNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (MemberBorrowException | BookBorrowEmptyException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT.value());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @PostMapping("/member/{memberId}/return/{bookId}")
    public ResponseEntity<?> returnBook(@PathVariable Long memberId, @PathVariable Long bookId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    memberMapper.toMemberResponse(borrowService.returnBook(bookId, memberId)));
        } catch (EntityNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (MemberBorrowException | BookBorrowEmptyException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT.value());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @GetMapping("/member/{name}/borrowed-books")
    public ResponseEntity<List<BookMemberDTO>> getBooksBorrowedByMemberName(@PathVariable String name) {
        return ResponseEntity.ok(borrowService.getBooksBorrowedByMemberName(name));
    }

    @GetMapping("/borrow-books/distinct-names")
    public ResponseEntity<List<String>> getAllBorrowedDistinctBookNames() {
        List<String> distinctBookNames = borrowService.getAllBorrowedDistinctBook();
        return ResponseEntity.ok(distinctBookNames);
    }


    @GetMapping("/borrowed-books/distinct-names-count")
    public ResponseEntity<Map<String, Long>> getAllBorrowedDistinctBookNamesWithCount() {
        Map<String, Long> distinctBookNamesWithCount = borrowService.getAllBorrowDistinctBookNamesWithCount();
        return ResponseEntity.ok(distinctBookNamesWithCount);
    }
}
