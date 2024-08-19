package com.techTask.library.exception;

public class BookBorrowEmptyException extends RuntimeException {
    public BookBorrowEmptyException() {
        super("Book is empty");
    }
}
