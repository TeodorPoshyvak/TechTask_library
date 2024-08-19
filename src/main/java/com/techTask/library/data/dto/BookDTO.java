package com.techTask.library.data.dto;

import java.util.HashSet;
import java.util.Set;

public class BookDTO {
    private Long ID;
    private String title;
    private String author;
    private int amount;
    private Set<MemberDTO> members = new HashSet<MemberDTO>();

    public BookDTO(Long ID, String title, String author, int amount) {
        this.ID = ID;
        this.title = title;
        this.author = author;
        this.amount = amount;
    }

    public BookDTO(String title, String author, int amount) {
        this.title = title;
        this.author = author;
        this.amount = amount;
    }

    public BookDTO() {
    }

    public Long getId() {
        return ID;
    }

    public void setId(Long ID) {
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Set<MemberDTO> getMembers() {
        return members;
    }

    public void setMembers(Set<MemberDTO> members) {
        this.members = members;
    }
}
