package com.techTask.library.data.request.book;

public class UpdateBookRequest extends BookRequest {

    public UpdateBookRequest() {
    }

    public UpdateBookRequest(String title, String author) {
        super(title, author);
    }
}
