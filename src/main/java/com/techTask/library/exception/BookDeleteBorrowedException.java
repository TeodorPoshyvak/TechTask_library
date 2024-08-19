package com.techTask.library.exception;

public class BookDeleteBorrowedException extends RuntimeException {
    public BookDeleteBorrowedException(String message) {
        super(message);
    }
}
