package com.techTask.library.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.DETACH;
import static jakarta.persistence.CascadeType.PERSIST;

@Entity
@Table(name = "Books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, message = "The title must be at least 3 symbols")
    @Pattern(regexp = "^[A-Z].*", message = "The title must start with a capital letter")
    private String title;

    @NotBlank(message = "Author is required")
    @Pattern(regexp = "^[A-Z][a-z]+\\s[A-Z][a-z]+$",
            message = "The author should contain two capital words with name and surname and space between")
    private String author;

    private int amount;

    @ManyToMany(cascade = {PERSIST, DETACH})
    @JoinTable(name = "manage_library",
            joinColumns = @JoinColumn(name = "book_ID"),
            inverseJoinColumns = @JoinColumn(name = "member_ID"))
    private Set<Member> members = new HashSet<Member>();

    public Book() {
    }

    public Book(Long id, String title, String author, int amount, Set<Member> members) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.amount = amount;
        this.members = members;
    }

    public Book(Long id, String title, String author, int amount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.amount = amount;
    }

    public Book(String title, String author, int amount) {
        this.title = title;
        this.author = author;
        this.amount = amount;
    }

    public Book(Long id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
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

    public Set<Member> getMembers() {
        return members;
    }

    public void setMembers(Set<Member> members) {
        this.members = members;
    }
}
