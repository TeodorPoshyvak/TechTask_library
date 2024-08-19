package com.techTask.library.data.request.book;

public class CreateBookRequest extends BookRequest {
    public CreateBookRequest() {}
    public CreateBookRequest(String title, String author) {
        super(title, author);
    }
}
