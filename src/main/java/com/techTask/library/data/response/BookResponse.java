package com.techTask.library.data.response;

import com.techTask.library.data.entity.Member;

import java.util.HashSet;
import java.util.Set;

public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private int amount;
    private Set<Member> members = new HashSet<Member>();

    public BookResponse() {

    }

    public BookResponse(Long id, String title, String author, int amount, Set<Member> members) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.amount = amount;
        this.members = members;
    }

    public Set<Member> getMembers() {
        return members;
    }

    public void setMembers(Set<Member> members) {
        this.members = members;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
