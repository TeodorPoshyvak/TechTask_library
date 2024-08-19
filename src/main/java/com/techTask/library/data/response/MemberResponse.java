package com.techTask.library.data.response;

import com.techTask.library.data.dto.BookMemberDTO;

import java.time.LocalDateTime;
import java.util.Set;

public class MemberResponse {
    long id;
    String name;
    LocalDateTime membership_date;
    private Set<BookMemberDTO> books;

    public MemberResponse() {
    }

    public MemberResponse(long id, String name, LocalDateTime membership_date, Set<BookMemberDTO> books) {
        this.id = id;
        this.name = name;
        this.membership_date = membership_date;
        this.books = books;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getMembership_date() {
        return membership_date;
    }

    public void setMembership_date(LocalDateTime membership_date) {
        this.membership_date = membership_date;
    }

    public Set<BookMemberDTO> getBooks() {
        return books;
    }

    public void setBooks(Set<BookMemberDTO> books) {
        this.books = books;
    }
}
