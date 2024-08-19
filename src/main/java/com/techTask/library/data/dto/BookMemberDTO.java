package com.techTask.library.data.dto;

import java.util.Objects;

public class BookMemberDTO {
    private Long ID;
    private String title;
    private String author;

    public BookMemberDTO() {
    }

    public BookMemberDTO(Long ID, String title, String author) {
        this.ID = ID;
        this.title = title;
        this.author = author;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookMemberDTO that = (BookMemberDTO) o;
        return Objects.equals(ID, that.ID) &&
                Objects.equals(title, that.title) &&
                Objects.equals(author, that.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, title, author);
    }
}
